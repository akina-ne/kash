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

@Controller
public class GameController {

  private final ImageRepository imageRepository;
  private final MessageService messageService;

  public GameController(ImageRepository imageRepository, MessageService messageService) {
    this.imageRepository = imageRepository;
    this.messageService = messageService;
  }

  @GetMapping("/game")
  public String game(Model model) {
    List<Image> imgs = imageRepository.findAll();
    model.addAttribute("images", imgs);
    if (!model.containsAttribute("answerForm")) {
      model.addAttribute("answerForm", new AnswerForm());
    }
    // 追加: ランダムに初期表示画像を選んでテンプレートに渡す
    String initialImage = null;
    if (imgs != null && !imgs.isEmpty()) {
      Image pick = imgs.get(ThreadLocalRandom.current().nextInt(imgs.size()));
      // filePath があればそれを使い、なければ /images/{fileName} を作る
      if (pick.getFilePath() != null && !pick.getFilePath().isBlank()) {
        initialImage = pick.getFilePath().startsWith("/") ? pick.getFilePath() : "/" + pick.getFilePath();
      } else {
        initialImage = "/images/" + pick.getFileName();
      }
    } else {
      // 画像がなければ既存のデフォルトを使う
      initialImage = "/images/onigiri.jpg";
    }
    model.addAttribute("initialImage", initialImage);

    return "game"; // templates/game.html
  }

  @PostMapping("/game/answer")
  public String answer(@ModelAttribute("answerForm") AnswerForm form, RedirectAttributes ra) {
    String content = form.getContent();
    if (content == null || content.trim().isEmpty()) {
      ra.addFlashAttribute("error", "回答を入力してください");
      ra.addFlashAttribute("answerForm", form);
      return "redirect:/game";
    }
    if (content.length() > 1000) {
      ra.addFlashAttribute("error", "回答は1000文字以内で入力してください");
      ra.addFlashAttribute("answerForm", form);
      return "redirect:/game";
    }
    messageService.saveMessage(content);
    ra.addFlashAttribute("saved", true);
    return "redirect:/game";
  }

  public static class AnswerForm {
    private String content;

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }
  }
}
