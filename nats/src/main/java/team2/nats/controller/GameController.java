// filepath: c:\Users\kenke\oithomes\isdev\kadai\isdev25\kash\nats\src\main\java\team2\nats\controller\GameController.java
package team2.nats.controller;

import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import team2.nats.entity.Image;
import team2.nats.repository.ImageRepository;
import team2.nats.service.MessageService;
import team2.nats.service.GameStatusService;
import team2.nats.service.CurrentQuestionService;
import team2.nats.service.ResultService;

@Controller
public class GameController {

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

  /** ゲーム画面表示 */
  @GetMapping("/game")
  public String game(Model model, HttpSession session) {

    // ★重要:
    // 不正解で redirect:/game しても「時間計測が継続」するように、
    // すでに開始時刻がある場合は上書きしない。
    if (session.getAttribute("quizStart") == null) {
      session.setAttribute("quizStart", System.currentTimeMillis());
    }

    List<Image> imgs = imageRepository.findAll();
    model.addAttribute("images", imgs);

    Image questionImage = null;

    // ★ まず「出題中の画像ID」があれば、それを優先的に使用する
    Long currentId = currentQuestionService.getCurrentImageId();
    if (currentId != null) {
      Optional<Image> byId = imageRepository.findById(currentId);
      if (byId.isPresent()) {
        questionImage = byId.get();
      }
    }

    // 出題中IDがない、または該当画像が見つからなかった場合は従来どおりランダム
    if (questionImage == null) {
      if (imgs != null && !imgs.isEmpty()) {
        questionImage = imgs.get(ThreadLocalRandom.current().nextInt(imgs.size()));
      } else {
        // 画像がなければ既存のデフォルトを使う
        Optional<Image> fallback = imageRepository.findById(1L);
        if (fallback.isPresent()) {
          questionImage = fallback.get();
        }
      }
    }

    // questionImage から initialImage のURLを生成
    String initialImage;
    if (questionImage != null) {
      if (questionImage.getFilePath() != null && !questionImage.getFilePath().isBlank()) {
        initialImage = questionImage.getFilePath().startsWith("/") ? questionImage.getFilePath()
            : "/" + questionImage.getFilePath();
      } else {
        initialImage = "/images/" + questionImage.getFileName();
      }
    } else {
      // 最終的な保険
      initialImage = "/images/onigiri.jpg";
    }

    model.addAttribute("questionImage", questionImage);
    model.addAttribute("initialImage", initialImage);

    // フォームオブジェクト
    if (!model.containsAttribute("answerForm")) {
      model.addAttribute("answerForm", new AnswerForm());
    }
    return "game";
  }

  /** 回答処理（ひらがなで判定＋正解時に結果保存） */
  @PostMapping("/game/answer")
  public String answer(@ModelAttribute("answerForm") AnswerForm form,
      RedirectAttributes ra,
      HttpSession session) {

    String content = form.getContent();

    // 入力チェック
    if (content == null || content.trim().isEmpty()) {
      ra.addFlashAttribute("error", "回答を入力してください（ひらがな）");
      ra.addFlashAttribute("answerForm", form);
      return "redirect:/game";
    }
    if (content.length() > 1000) {
      ra.addFlashAttribute("error", "回答は1000文字以内で入力してください");
      ra.addFlashAttribute("answerForm", form);
      return "redirect:/game";
    }

    // 前後の空白を削除
    String normalized = content.trim();

    // ひらがなのみ許可
    if (!isHiraganaOnly(normalized)) {
      ra.addFlashAttribute("error", "ひらがなのみで入力してください");
      ra.addFlashAttribute("answerForm", form);
      return "redirect:/game";
    }

    // どの画像への回答か（hidden imageId）
    Long imageId = form.getImageId();
    if (imageId == null) {
      ra.addFlashAttribute("error", "画像情報が取得できませんでした。もう一度お試しください。");
      ra.addFlashAttribute("answerForm", form);
      return "redirect:/game";
    }

    Optional<Image> imageOpt = imageRepository.findById(imageId);
    if (imageOpt.isEmpty()) {
      ra.addFlashAttribute("error", "対象の画像が見つかりませんでした");
      ra.addFlashAttribute("answerForm", form);
      return "redirect:/game";
    }

    Image image = imageOpt.get();
    String answerKana = image.getAnswerKana(); // ここにはひらがなが入る

    if (answerKana == null || answerKana.isBlank()) {
      ra.addFlashAttribute("error", "この画像の正解が未設定です");
      ra.addFlashAttribute("answerForm", form);
      return "redirect:/game";
    }

    // ひらがなで完全一致判定
    boolean correct = normalized.equals(answerKana.trim());

    // 入力内容はこれまで通り保存
    messageService.saveMessage(content);

    // 正解した場合だけ、/game表示からの経過時間を計算して results テーブルに保存
    if (correct) {
      Object startObj = session.getAttribute("quizStart");
      if (startObj instanceof Long) {
        long startMs = (Long) startObj;
        long nowMs = System.currentTimeMillis();
        long elapsedMs = nowMs - startMs;
        if (elapsedMs < 0) {
          elapsedMs = 0L;
        }

        // ログインユーザー名取得（未ログインなら anonymous）
        String participant = "anonymous";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
          participant = auth.getName();
        }

        try {
          resultService.saveResult(participant, elapsedMs, true);
        } catch (Exception e) {
          e.printStackTrace();
        }

        // ★正解したときだけリセット（不正解時は継続）
        session.removeAttribute("quizStart");
      }
    }

    // 結果メッセージ
    if (correct) {
      ra.addFlashAttribute("resultMsg", "正解です！");
    } else {
      ra.addFlashAttribute("resultMsg", "不正解です");
    }
    ra.addFlashAttribute("answerForm", form);
    ra.addFlashAttribute("saved", true);
    return "redirect:/game";
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

  /** フォーム用のインナークラス */
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

  /**
   * ゲームを開始するAPI.
   * 「開始」ボタン押下時に1Pから呼び出されることを想定.
   *
   * @return started=true を含むレスポンス
   */
  @PostMapping("/api/game/start")
  @ResponseBody
  public Map<String, Object> startGame() {
    // 画像一覧からランダム1枚選択して「出題中画像」として保存
    List<Image> imgs = imageRepository.findAll();
    Long pickedId = null;
    if (imgs != null && !imgs.isEmpty()) {
      Image pick = imgs.get(ThreadLocalRandom.current().nextInt(imgs.size()));
      pickedId = pick.getId();
      currentQuestionService.setCurrentImageId(pickedId);
    } else {
      // 画像が無い場合は、ID=1 を仮に使う（存在しないなら null のまま）
      Optional<Image> fallback = imageRepository.findById(1L);
      if (fallback.isPresent()) {
        pickedId = fallback.get().getId();
        currentQuestionService.setCurrentImageId(pickedId);
      } else {
        currentQuestionService.resetCurrentImageId();
      }
    }

    // ゲーム開始フラグを立てる（一定時間だけ true）
    gameStatusService.startGame();

    Map<String, Object> response = new HashMap<>();
    response.put("started", true);
    response.put("imageId", pickedId);
    return response;
  }

  /**
   * ゲームが開始されたかどうかを取得するAPI.
   * 全ユーザーが一定間隔でポーリングして利用する.
   *
   * @return started: true/false
   */
  @GetMapping("/api/game/status")
  @ResponseBody
  public Map<String, Object> getGameStatus() {
    boolean started = gameStatusService.isGameStarted();

    Map<String, Object> response = new HashMap<>();
    response.put("started", started);
    return response;
  }
}
