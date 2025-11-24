package team2.nats.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ActiveUserService {
  // 追加順を保持する Set（重複不可）
  private final Set<String> users = new LinkedHashSet<>();

  public void userJoined(String username) {
    if (username == null)
      return;
    // 既に存在する場合は一度削除して末尾に再追加（順序更新）
    if (users.contains(username)) {
      users.remove(username);
    }
    users.add(username);
  }

  public void userLeft(String username) {
    if (username == null)
      return;
    users.remove(username);
  }

  public List<String> getOrderedUsers() {
    return new ArrayList<>(users);
  }
}
