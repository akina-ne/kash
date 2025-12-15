package team2.nats.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import team2.nats.security.LoginSuccessHandler;
import team2.nats.security.LogoutSuccessHandlerImpl;

@Configuration
public class SecurityConfig {

  @Bean
  public InMemoryUserDetailsManager userDetailsService() {
    // in-memory ユーザを登録。パスワードは起動時に BCrypt でエンコードされる
    UserDetails kash = User.withUsername("kash")
        .password("{bcrypt}$2y$05$uOIjgUhmJ.17O7z7jjl4YOqIkjw5is0BF7lxhj43j66mfhAeUcVvS")
        .roles("USER").build();

    UserDetails ken = User.withUsername("ken")
        .password("{bcrypt}$2y$05$7z1pcq6GEB9QGHAx/lb/nO7SodBfJWp2uRBAW1zj3SkMDknzRVkoi")
        .roles("USER").build();

    UserDetails shimo = User.withUsername("shimo")
        .password("{bcrypt}$2y$05$dCINWd5hmm97l.xrBY8ISuo38MtIcap9comkW6KBds6l0EXYpK7X2")
        .roles("USER").build();

    UserDetails akina = User.withUsername("akina")
        .password("{bcrypt}$2y$05$zduLMOUz15omQ42cPEvSI.FIH2//GXJ.sN4qLG3brhUYpK.8c.Q4W")
        .roles("USER").build();

    UserDetails tsuna = User.withUsername("tsuna")
        .password("{bcrypt}$2y$05$nz9no3aAc.9WN6RU.CbSbeScoi8bVr0bxs0Vj8ISiCt6at5wg/0U.")
        .roles("USER").build();

    return new InMemoryUserDetailsManager(kash, ken, shimo, akina, tsuna);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      LoginSuccessHandler loginSuccessHandler,
      LogoutSuccessHandlerImpl logoutSuccessHandler) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
            .requestMatchers("/api/online").authenticated()
            .requestMatchers("/api/game/**").authenticated()
            .anyRequest().authenticated())
        .formLogin(login -> login
            .successHandler(loginSuccessHandler)
            .permitAll())
        .logout(logout -> logout
            .logoutSuccessHandler(logoutSuccessHandler)
            .logoutUrl("/logout")
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)
            .permitAll())
        .csrf(csrf -> csrf
            // AntPathRequestMatcher を使わずに、パス指定で直接除外
            .ignoringRequestMatchers("/h2-console/**", "/api/game/**"))
        .headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions.sameOrigin()));
    return http.build();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }
}
