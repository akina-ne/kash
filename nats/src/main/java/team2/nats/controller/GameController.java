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

  @GetMapping("/game")
  public String game(
      Model model,
      HttpSession session,
      @RequestParam(value = "reset", required = false) Integer reset) {

    if (reset != null && reset.intValue() == 1) {
      session.removeAttribute("quizStart");
    }

    if (session.getAttribute("quizStart") == null) {
      session.setAttribute("quizStart", System.currentTimeMillis());
    }

    List<Image> imgs = imageRepository.findAll();
    model.addAttribute("images", imgs);

    Image questionImage = null;

    Long currentId = currentQuestionService.getCurrentImageId();
    if (currentId != null) {
      Optional<Image> byId = imageRepository.findById(currentId);
      if (byId.isPresent()) {
        questionImage = byId.get();
      }
    }

    if (questionImage == null) {
      if (imgs != null && !imgs.isEmpty()) {
        questionImage = imgs.get(ThreadLocalRandom.current().nextInt(imgs.size()));
      } else {
        Optional<Image> fallback = imageRepository.findById(1L);
        if (fallback.isPresent()) {
          questionImage = fallback.get();
        }
      }
    }

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
  public Map<String, Object> answer(@ModelAttribute("answerForm") AnswerForm form,
      HttpSession session) {

    Map<String, Object> response = new HashMap<>();

    String content = form.getContent();

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

    boolean correct = normalized.equals(answerKana.trim());

    messageService.saveMessage(content);

    if (correct) {
      Object startObj = session.getAttribute("quizStart");
      if (startObj instanceof Long) {
        long startMs = (Long) startObj;
        long nowMs = System.currentTimeMillis();
        long elapsedMs = nowMs - startMs;
        if (elapsedMs < 0)
          elapsedMs = 0L;

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

        session.removeAttribute("quizStart");
      }
    }

    response.put("success", true);
    response.put("correct", correct);
    response.put("message", correct ? "正解です！" : "不正解です");
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

  @PostMapping("/api/game/start")
  @ResponseBody
  public Map<String, Object> startGame() {
    List<Image> imgs = imageRepository.findAll();
    Long pickedId = null;
    if (imgs != null && !imgs.isEmpty()) {
      Image pick = imgs.get(ThreadLocalRandom.current().nextInt(imgs.size()));
      pickedId = pick.getId();
      currentQuestionService.setCurrentImageId(pickedId);
    } else {
      Optional<Image> fallback = imageRepository.findById(1L);
      if (fallback.isPresent()) {
        pickedId = fallback.get().getId();
        currentQuestionService.setCurrentImageId(pickedId);
      } else {
        currentQuestionService.resetCurrentImageId();
      }
    }

    gameStatusService.startGame();

    Map<String, Object> response = new HashMap<>();
    response.put("started", true);
    response.put("imageId", pickedId);
    return response;
  }

  @PostMapping("/api/game/start-countdown")
  @ResponseBody
  public Map<String, Object> startCountdown(
      @RequestParam(value = "durationMillis", required = false) Long durationMillis) {
    long duration = (durationMillis == null) ? 3000L : durationMillis.longValue();

    List<Image> imgs = imageRepository.findAll();
    Long pickedId = null;
    if (imgs != null && !imgs.isEmpty()) {
      Image pick = imgs.get(ThreadLocalRandom.current().nextInt(imgs.size()));
      pickedId = pick.getId();
      currentQuestionService.setCurrentImageId(pickedId);
    } else {
      Optional<Image> fallback = imageRepository.findById(1L);
      if (fallback.isPresent()) {
        pickedId = fallback.get().getId();
        currentQuestionService.setCurrentImageId(pickedId);
      } else {
        currentQuestionService.resetCurrentImageId();
      }
    }

    gameStatusService.startCountdown(duration);

    Map<String, Object> response = new HashMap<>();
    response.put("countdownMillis", duration);
    response.put("imageId", pickedId);
    response.put("started", false);
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
    currentQuestionService.resetCurrentImageId();
    gameStatusService.resetGame();

    // ★追加：ランキング（results）もリセット
    resultService.resetRankings();

    // ★全員へ通知（2秒間 true）
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
}
