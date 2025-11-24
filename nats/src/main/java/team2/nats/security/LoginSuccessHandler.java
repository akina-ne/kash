package team2.nats.security;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import team2.nats.service.ActiveUserService;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ActiveUserService activeUserService;

  public LoginSuccessHandler(ActiveUserService activeUserService) {
    this.activeUserService = activeUserService;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    activeUserService.userJoined(authentication.getName());
    response.sendRedirect(request.getContextPath() + "/");
  }
}
