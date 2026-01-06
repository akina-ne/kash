package team2.nats.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import team2.nats.dto.RankingDto;
import team2.nats.entity.Image;
import team2.nats.repository.ImageRepository;
import team2.nats.service.MessageService;
import team2.nats.service.GameStatusService;
import team2.nats.service.CurrentQuestionService;
import team2.nats.service.ResultService;

@Controller
public class GameController {

  // ★追加：個人進行用セッションキー
  private static final String SESSION_QUESTION_INDEX = "questionIndex";
  private static final String SESSION_QUESTION_START_MS = "questionStartMs";
  private static final String SESSION_TOTAL_ELAPSED_MS = "totalElapsedMs";

  // ★問題数（変えるならここ）
  private static final int QUESTIONS_PER_GAME = 5;

  private final ImageRepository imageRepository;
  private final MessageService messageService;
  private final GameStatusService gameStatusService;
  private final CurrentQuestionService currentQuestionService;
  private final ResultService resultService;

  public GameController(ImageRepository imageRepository,
      MessageService messageService,
      GameStatusService gameStatusService,
      CurrentQuestionService currentQuestionService,
      ResultService resultService) {
    this.imageRepository = imageRepository;
    this.messageService = messageService;
    this.gameStatusService = gameStatusService;
    this.currentQuestionService = currentQuestionService;
    this.resultService = resultService;
  }

  /** ★追加：共有の5問セットを未作成なら作る（重複なし） */
  private void ensureSharedQuestionSetInitialized() {
    if (currentQuestionService.hasQuestionSet())
      return;

    List<Image> imgs = imageRepository.findAll();
    ArrayList<Long> ids = new ArrayList<>();
    if (imgs != null) {
      for (Image img : imgs) {
        if (img != null && img.getId() != null)
          ids.add(img.getId());
      }
    }

    Collections.shuffle(ids);
    int n = Math.min(QUESTIONS_PER_GAME, ids.size());
    List<Long> picked = ids.subList(0, n);

    currentQuestionService.setQuestionSet(picked);
    currentQuestionService.setCurrentImageId(currentQuestionService.getQuestionImageIdAt(0));
  }

  @GetMapping("/game")
  public String game(Model model, HttpSession session,
      @RequestParam(value = "reset", required = false) Integer reset) {

    if (reset != null && reset.intValue() == 1) {
      session.removeAttribute("quizStart");
      session.removeAttribute(SESSION_QUESTION_INDEX);
      session.removeAttribute(SESSION_QUESTION_START_MS);
      session.removeAttribute(SESSION_TOTAL_ELAPSED_MS);
    }

    // ★共有の出題順を確定（全員共通）
    ensureSharedQuestionSetInitialized();

    // ★個人進行の初期化
    if (session.getAttribute(SESSION_QUESTION_INDEX) == null) {
      session.setAttribute(SESSION_QUESTION_INDEX, 0);
    }
    if (session.getAttribute(SESSION_TOTAL_ELAPSED_MS) == null) {
      session.setAttribute(SESSION_TOTAL_ELAPSED_MS, 0L);
    }

    int idx = (Integer) session.getAttribute(SESSION_QUESTION_INDEX);

    // ★この人が表示すべき問題（共有セット[idx]）
    Long imageId = currentQuestionService.getQuestionImageIdAt(idx);
    Image questionImage = null;
    if (imageId != null) {
      questionImage = imageRepository.findById(imageId).orElse(null);
    }

    // currentImageId は「現在の問題」として更新（共有は保ちつつ参照用）
    currentQuestionService.setCurrentImageId(imageId);

    // ★1問ごとの開始時刻（合計計測用）
    long startMs = System.currentTimeMillis();
    session.setAttribute(SESSION_QUESTION_START_MS, startMs);
    session.setAttribute("quizStart", startMs); // 既存互換（残す）

    List<Image> imgs = imageRepository.findAll();
    model.addAttribute("images", imgs);

    String initialImage;
    if (questionImage != null) {
      if (questionImage.getFilePath() != null && !questionImage.getFilePath().isBlank()) {
        initialImage = questionImage.getFilePath().startsWith("/") ? questionImage.getFilePath()
            : "/" + questionImage.getFilePath();
      } else {
        initialImage = "/images/" + questionImage.getFileName();
      }
    } else {
      initialImage = "/images/onigiri.jpg";
    }

    model.addAttribute("questionImage", questionImage);
    model.addAttribute("initialImage", initialImage);

    if (!model.containsAttribute("answerForm")) {
      model.addAttribute("answerForm", new AnswerForm());
    }
    return "game";
  }

  @PostMapping("/game/answer")
  @ResponseBody
  public Map<String, Object> answer(@ModelAttribute("answerForm") AnswerForm form, HttpSession session) {

    Map<String, Object> response = new HashMap<>();

    String content = form.getContent();
    boolean timeout = "__TIMEUP__".equals(content); // ★ タイムアップ特別値かどうか

    // ★タイムアップ以外のときだけ入力バリデーションを行う
    if (!timeout) {
      if (content == null || content.trim().isEmpty()) {
        response.put("success", false);
        response.put("error", "回答を入力してください（ひらがな）");
        return response;
      }
      if (content.length() > 1000) {
        response.put("success", false);
        response.put("error", "回答は1000文字以内で入力してください");
        return response;
      }

      String normalized = content.trim();
      if (!isHiraganaOnly(normalized)) {
        response.put("success", false);
        response.put("error", "ひらがなのみで入力してください");
        return response;
      }
    }

    Long imageId = form.getImageId();
    if (imageId == null) {
      response.put("success", false);
      response.put("error", "画像情報が取得できませんでした。もう一度お試しください。");
      return response;
    }

    Optional<Image> imageOpt = imageRepository.findById(imageId);
    if (imageOpt.isEmpty()) {
      response.put("success", false);
      response.put("error", "対象の画像が見つかりませんでした");
      return response;
    }

    Image image = imageOpt.get();
    String answerKana = image.getAnswerKana();
    if (answerKana == null || answerKana.isBlank()) {
      response.put("success", false);
      response.put("error", "この画像の正解が未設定です");
      return response;
    }

    boolean correct;
    if (timeout) {
      // タイムアップは常に不正解扱い
      correct = false;
    } else {
      String normalized = content.trim();
      correct = normalized.equals(answerKana.trim());
      // 通常回答のみメッセージ保存
      messageService.saveMessage(form.getContent());
    }

    // ★ここから「時間計測＆インデックス更新」

    long nowMs = System.currentTimeMillis();
    long qStartMs = (session.getAttribute(SESSION_QUESTION_START_MS) instanceof Long)
        ? (Long) session.getAttribute(SESSION_QUESTION_START_MS)
        : nowMs;
    long elapsedMs = Math.max(0L, nowMs - qStartMs);

    long totalMs = (session.getAttribute(SESSION_TOTAL_ELAPSED_MS) instanceof Long)
        ? (Long) session.getAttribute(SESSION_TOTAL_ELAPSED_MS)
        : 0L;

    int idx = (session.getAttribute(SESSION_QUESTION_INDEX) instanceof Integer)
        ? (Integer) session.getAttribute(SESSION_QUESTION_INDEX)
        : 0;

    // ★★★ ここが重要 ★★★
    // ・正解 or タイムアップのときだけ「1問終わり」として時間加算＆インデックスを進める
    // ・通常不正解（timeout=false && correct=false）のときは何も進めない
    if (correct || timeout) {
      totalMs += elapsedMs;
      session.setAttribute(SESSION_TOTAL_ELAPSED_MS, totalMs);

      idx++;
      session.setAttribute(SESSION_QUESTION_INDEX, idx);
    }

    int totalQuestions = Math.min(QUESTIONS_PER_GAME, currentQuestionService.getQuestionSetSize());

    // ユーザ名取得
    String participant = "anonymous";
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getName() != null) {
      participant = auth.getName();
    }

    // ★ゲーム終了判定も「インデックスを進めた場合のみ」効くことになる
    if (idx >= totalQuestions) {
      // ゲーム終了：合計時間を登録（全問に到達したときのみ）
      resultService.saveResult(participant, totalMs, true);

      // 個人状態をクリア
      session.removeAttribute("quizStart");
      session.removeAttribute(SESSION_QUESTION_INDEX);
      session.removeAttribute(SESSION_QUESTION_START_MS);
      session.removeAttribute(SESSION_TOTAL_ELAPSED_MS);

      response.put("success", true);
      response.put("correct", correct);
      // メッセージは正解/タイムアップで適宜
      if (correct) {
        response.put("message", "正解です！");
      } else if (timeout) {
        response.put("message", "タイムアップ（不正解）です");
      } else {
        response.put("message", "不正解です");
      }
      response.put("redirectTo", "/ranking");
      response.put("timeout", timeout);
      return response;
    }

    // ★まだ問題が残っている場合
    response.put("success", true);
    response.put("correct", correct);
    if (correct) {
      response.put("message", "正解です！");
    } else if (timeout) {
      response.put("message", "タイムアップ（不正解）です");
    } else {
      response.put("message", "不正解です");
    }

    // 正解 or タイムアップの場合のみ front 側が /game へ遷移する想定
    response.put("redirectTo", "/game");
    response.put("timeout", timeout);
    return response;
  }

  private boolean isHiraganaOnly(String s) {
    if (s == null || s.isEmpty())
      return false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if ((c < 'ぁ' || c > 'ん') && c != 'ー') {
        return false;
      }
    }
    return true;
  }

  @PostMapping("/api/game/start")
  @ResponseBody
  public Map<String, Object> startGame() {
    // ★ガード：開始後の再押下で問題セットを作り直さない
    if (!currentQuestionService.hasQuestionSet()) {
      ensureSharedQuestionSetInitialized();
      gameStatusService.startGame();
    }

    Map<String, Object> response = new HashMap<>();
    response.put("started", true);
    response.put("imageId", currentQuestionService.getCurrentImageId());
    response.put("ignored", currentQuestionService.hasQuestionSet()); // 参考情報
    return response;
  }

  @PostMapping("/api/game/start-countdown")
  @ResponseBody
  public Map<String, Object> startCountdown(
      @RequestParam(value = "durationMillis", required = false) Long durationMillis) {

    long duration = (durationMillis == null) ? 3000L : durationMillis.longValue();

    // ★ガード：開始後の再押下で問題セットを作り直さない／再カウントダウンしない
    if (!currentQuestionService.hasQuestionSet()) {
      ensureSharedQuestionSetInitialized();
      gameStatusService.startCountdown(duration);
    }

    Map<String, Object> response = new HashMap<>();
    response.put("countdownMillis", duration);
    response.put("imageId", currentQuestionService.getCurrentImageId());
    response.put("started", false);
    response.put("ignored", currentQuestionService.hasQuestionSet()); // 参考情報
    return response;
  }

  @GetMapping("/api/game/status")
  @ResponseBody
  public Map<String, Object> getGameStatus() {
    boolean started = gameStatusService.isGameStarted();
    long remaining = gameStatusService.getCountdownRemainingMillis();
    boolean resetRequested = gameStatusService.isResetRequested();

    Map<String, Object> response = new HashMap<>();
    response.put("started", started);
    response.put("remainingMillis", remaining);
    response.put("resetRequested", resetRequested);
    return response;
  }

  @PostMapping("/api/game/reset")
  @ResponseBody
  public Map<String, Object> resetGame(HttpSession session) {
    session.removeAttribute("quizStart");
    session.removeAttribute(SESSION_QUESTION_INDEX);
    session.removeAttribute(SESSION_QUESTION_START_MS);
    session.removeAttribute(SESSION_TOTAL_ELAPSED_MS);

    // ★共有もリセット（次のゲームで別の5問セット）
    currentQuestionService.resetAll();
    gameStatusService.resetGame();

    resultService.resetRankings();
    gameStatusService.requestReset();

    Map<String, Object> response = new HashMap<>();
    response.put("ok", true);
    return response;
  }

  @GetMapping("/api/rankings")
  @ResponseBody
  public List<RankingDto> rankings() {
    return resultService.getRankings();
  }

  public static class AnswerForm {
    private Long imageId;
    private String content;

    public Long getImageId() {
      return imageId;
    }

    public void setImageId(Long imageId) {
      this.imageId = imageId;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }
  }
}
