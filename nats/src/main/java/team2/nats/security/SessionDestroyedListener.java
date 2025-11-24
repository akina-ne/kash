package team2.nats.security;

import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

@Component
public class SessionDestroyedListener implements ApplicationListener<SessionDestroyedEvent> {
  @Override
  public void onApplicationEvent(SessionDestroyedEvent event) {
    // 何もしない（実装を簡素化）
  }
}
