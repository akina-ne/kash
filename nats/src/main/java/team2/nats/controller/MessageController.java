package team2.nats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import team2.nats.service.MessageService;
import team2.nats.service.ResultService;
import team2.nats.model.Message;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpSession;

@Controller
public class MessageController {

  private final MessageService messageService;
  private final ResultService resultService;

  public MessageController(MessageService messageService, ResultService resultService) {
    this.messageService = messageService;
    this.resultService = resultService;
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
      RedirectAttributes redirectAttributes, HttpSession session) {

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

    // Message を保存し、createdAt を取得
    Message saved = messageService.saveMessage(content);

    // セッションの quizStart を読み出して elapsed を算出し Result に保存
    Object startObj = session.getAttribute("quizStart");
    if (startObj instanceof Long) {
      long startMs = (Long) startObj;
      LocalDateTime startLdt = Instant.ofEpochMilli(startMs).atZone(ZoneId.systemDefault()).toLocalDateTime();
      LocalDateTime createdAt = saved.getCreatedAt();
      long elapsedMs = ChronoUnit.MILLIS.between(startLdt, createdAt);

      // 簡易的な妥当性チェック
      if (elapsedMs < 0) {
        // 時刻不整合ならログのみ（実運用では警告）
        elapsedMs = 0L;
      }

      // ユーザ名は認証情報から取得
      String participant = "anonymous";
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth != null && auth.getName() != null) {
        participant = auth.getName();
      }

      // 正誤判定: TODO 実際の判定ロジックに置き換える
      boolean correct = true;

      // 結果を保存
      try {
        resultService.saveResult(participant, elapsedMs, correct);
      } catch (Exception e) {
        // 保存失敗はログにして続行
        e.printStackTrace();
      }

      // セッションの start をクリア
      session.removeAttribute("quizStart");
    }

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
