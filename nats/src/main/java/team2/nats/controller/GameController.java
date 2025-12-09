// filepath: c:\Users\kenke\oithomes\isdev\kadai\isdev25\kash\nats\src\main\java\team2\nats\controller\GameController.java
package team2.nats.controller;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import team2.nats.entity.Image;
import team2.nats.repository.ImageRepository;
import team2.nats.service.MessageService;
import java.util.List;

import java.util.Optional;

@Controller
public class GameController {

  private final ImageRepository imageRepository;
  private final MessageService messageService;

  public GameController(ImageRepository imageRepository, MessageService messageService) {
    this.imageRepository = imageRepository;
    this.messageService = messageService;
  }

  /** ゲーム画面表示（おにぎり固定・id=1 を出題） */
  @GetMapping("/game")
  public String game(Model model) {
    List<Image> imgs = imageRepository.findAll();
    model.addAttribute("images", imgs);

    // 追加: ランダムに初期表示画像を選んでテンプレートに渡す
    String initialImage = null;
    if (imgs != null && !imgs.isEmpty()) {
      Image pick = imgs.get(ThreadLocalRandom.current().nextInt(imgs.size()));
      // filePath があればそれを使い、なければ /images/{fileName} を作る
      model.addAttribute("questionImage", pick);
      if (pick.getFilePath() != null && !pick.getFilePath().isBlank()) {
        initialImage = pick.getFilePath().startsWith("/") ? pick.getFilePath() : "/" + pick.getFilePath();
      } else {
        initialImage = "/images/" + pick.getFileName();
      }
    } else {
      // 画像がなければ既存のデフォルトを使う
      initialImage = "/images/onigiri.jpg";
      model.addAttribute("questionImage", imageRepository.findById(1L));
    }
    model.addAttribute("initialImage", initialImage);

    // フォームオブジェクト
    if (!model.containsAttribute("answerForm")) {
      model.addAttribute("answerForm", new AnswerForm());
    }
    return "game";
  }

  /** 回答処理（ローマ字で判定） */
  @PostMapping("/game/answer")
  public String answer(@ModelAttribute("answerForm") AnswerForm form, RedirectAttributes ra) {

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

    // ★ ひらがなのみ許可
    if (!isHiraganaOnly(normalized)) {
      ra.addFlashAttribute("error", "ひらがなのみで入力してください");
      ra.addFlashAttribute("answerForm", form);
      return "redirect:/game";
    }

    // どの画像への回答か（hidden imageId）
    Long imageId = form.getImageId();
    if (imageId == null) {
      // ★ ここでエラーにせず、メッセージを出して戻す
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

    // ★ ひらがなで完全一致判定
    boolean correct = normalized.equals(answerKana.trim());

    // 入力内容はこれまで通り保存
    messageService.saveMessage(content);

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
      if (c < 'ぁ' || c > 'ん') {
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
}
