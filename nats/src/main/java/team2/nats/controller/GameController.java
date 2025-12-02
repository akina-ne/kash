package team2.nats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import team2.nats.repository.ImageRepository;
import team2.nats.service.MessageService;

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
    model.addAttribute("images", imageRepository.findAll());
    if (!model.containsAttribute("answerForm")) {
      model.addAttribute("answerForm", new AnswerForm());
    }
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
