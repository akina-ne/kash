package team2.nats.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
    // in-memory ユーザを登録。パスワードは起動時に BCrypt でエンコードされる
    UserDetails user = User.withUsername("kash")
        .password(passwordEncoder.encode("nats"))
        .roles("USER")
        .build();
    return new InMemoryUserDetailsManager(user);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .anyRequest().authenticated())
        // デフォルトのログインページを使用
        .formLogin(form -> form.permitAll())
        .logout(logout -> logout.permitAll());

    return http.build();
  }
}
