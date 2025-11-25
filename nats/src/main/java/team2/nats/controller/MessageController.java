package team2.nats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import team2.nats.service.MessageService;

@Controller
public class MessageController {

  private final MessageService messageService;

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @GetMapping("/message/form")
  public String showForm(Model model) {
    if (!model.containsAttribute("messageForm")) {
      model.addAttribute("messageForm", new MessageForm());
    }
    return "messageForm";
  }

  @PostMapping("/message/save")
  public String saveMessage(@ModelAttribute("messageForm") MessageForm messageForm,
      RedirectAttributes redirectAttributes) {

    String content = messageForm.getContent();
    // 手動バリデーション
    if (content == null || content.trim().isEmpty()) {
      redirectAttributes.addFlashAttribute("error", "メッセージを入力してください");
      redirectAttributes.addFlashAttribute("messageForm", messageForm);
      return "redirect:/message/form";
    }
    if (content.length() > 1000) {
      redirectAttributes.addFlashAttribute("error", "メッセージは1000文字以内で入力してください");
      redirectAttributes.addFlashAttribute("messageForm", messageForm);
      return "redirect:/message/form";
    }

    messageService.saveMessage(content);
    redirectAttributes.addFlashAttribute("saved", true);
    return "redirect:/message/form";
  }

  // フォーム DTO
  public static class MessageForm {

    private String content;

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }
  }
}
