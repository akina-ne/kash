package team2.nats.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.nats.service.ActiveUserService;

@RestController
public class OnlineApiController {
  private final ActiveUserService activeUserService;

  public OnlineApiController(ActiveUserService activeUserService) {
    this.activeUserService = activeUserService;
  }

  @GetMapping("/api/online")
  public Map<String, Object> online() {
    // 入室順のユーザー一覧
    List<String> users = activeUserService.getOrderedUsers();

    // 現在ログイン中のユーザー名を取得
    String currentUser = null;
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getName() != null) {
      currentUser = auth.getName();
    }

    // JSON オブジェクトとして返す
    Map<String, Object> response = new HashMap<>();
    response.put("users", users);
    response.put("currentUser", currentUser);
    return response;
  }
}
