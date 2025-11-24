package team2.nats.security;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import team2.nats.service.ActiveUserService;

@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

  private final ActiveUserService activeUserService;

  public LogoutSuccessHandlerImpl(ActiveUserService activeUserService) {
    this.activeUserService = activeUserService;
  }

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    if (authentication != null) {
      activeUserService.userLeft(authentication.getName());
    }
    if (request != null) {
      var session = request.getSession(false);
      if (session != null) {
        session.invalidate();
      }
    }
    response.sendRedirect(request.getContextPath() + "/login?logout");
  }
}
