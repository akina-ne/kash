package team2.nats.controller;

import java.util.List;
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
  public List<String> online() {
    return activeUserService.getOrderedUsers();
  }
}
