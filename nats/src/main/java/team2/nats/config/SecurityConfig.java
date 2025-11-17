package team2.nats.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

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
