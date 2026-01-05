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

    UserDetails Z2762 = User.withUsername("Z2762")
        .password("{bcrypt}$2y$05$JjVGtr/1bQqHi0OK7GjRUuIsTCK1tR4r/EN.gS4s52wZIdQTRthi2")
        .roles("USER").build();
    UserDetails Z2789 = User.withUsername("Z2789")
        .password("{bcrypt}$2y$05$XgANuuRAVctEV4lpFEaUA.9trP2PZsYZvRzPjvnMMV85B2c3G1C2e")
        .roles("USER").build();
    UserDetails Z2819 = User.withUsername("Z2819")
        .password("{bcrypt}$2y$05$id7UHGyOLLTMPWGggqnKdepx2O.IaLz5o/TxgJkHosUfQYHahuOxC")
        .roles("USER").build();
    UserDetails Z2911 = User.withUsername("Z2911")
        .password("{bcrypt}$2y$05$eLXMYkpH4IdC2MbREeVr7.KLFDtnbWao4Bw3.Aff1uQxXXXNMpB5G")
        .roles("USER").build();
    UserDetails Z2946 = User.withUsername("Z2946")
        .password("{bcrypt}$2y$05$XSZdTje2Rb/0oZZG1meLg.TvP3cL3v2bUWENoxTxQ8kaijKZQ7W9e")
        .roles("USER").build();
    UserDetails Z2974 = User.withUsername("Z2974")
        .password("{bcrypt}$2y$05$P5.bktxMnmX4o3V7vzOg7OZiHlKOk.IZC4R3n.78oJVJJR8reiPD6")
        .roles("USER").build();
    UserDetails Z3052 = User.withUsername("Z3052")
        .password("{bcrypt}$2y$05$Gm3109HRJJEwVxnD4QYvCuUeOergko5lrHqkyOyEQZqPXRLiMMShW")
        .roles("USER").build();
    UserDetails Z3055 = User.withUsername("Z3055")
        .password("{bcrypt}$2y$05$SLtMSMq/hnn80Nv.e3FYP.PBbOWm3F3r8SjBByOJO6ugGlDDvpH4K")
        .roles("USER").build();
    UserDetails Z3065 = User.withUsername("Z3065")
        .password("{bcrypt}$2y$05$AoDLZxu5lK9AEdwol8l4au0tUY7z7yhjo95VmJHKqG/zaHgpk6PZu")
        .roles("USER").build();
    UserDetails Z3069 = User.withUsername("Z3069")
        .password("{bcrypt}$2y$05$r9MSy2wnPa4hF7KP.AOtqeCKJ1Y1XUAE5lGHRLhEayRpquXcij0H6")
        .roles("USER").build();
    UserDetails Z3182 = User.withUsername("Z3182")
        .password("{bcrypt}$2y$05$e1og2Sbwt4U54RN8WQIKDOykO.NDagp4d0U/2y64jGpUzWil2NcJe")
        .roles("USER").build();
    UserDetails Z4272 = User.withUsername("Z4272")
        .password("{bcrypt}$2y$05$yFB0cU7ro00gW7Xz8Swmoe9w.D3StHKUHfhMgkysXlbO0cY7xK3aC")
        .roles("USER").build();
    UserDetails 本田 = User.withUsername("本田")
        .password("{bcrypt}$2y$05$4eqBfkhKiDziTien.BlZh.e8ak9C.AQ1jApdQNaSj1rdmVnKQrOCO")
        .roles("USER").build();
    UserDetails 福安 = User.withUsername("福安")
        .password("{bcrypt}$2y$05$FjBaXS65eKr.0NOYEHICnu0/LRa.vAaz58lJwFxWNDJC9q7tNn0Ui")
        .roles("USER").build();
    UserDetails 井垣 = User.withUsername("井垣")
        .password("{bcrypt}$2y$05$7/DLePSJhtFbfwqjCeP5pO2kfTi8JkS5oHiItDx8l0pajJ.M5PzTu")
        .roles("USER").build();
    UserDetails Z2883 = User.withUsername("Z2883")
        .password("{bcrypt}$2y$05$CRK/KECwHaLnHaXSBUMjkOCMHCKoj53d7UF7.Dn2x7kLKdtb4VS2S")
        .roles("USER").build();
    UserDetails Z3193 = User.withUsername("Z3193")
        .password("{bcrypt}$2y$05$.7x70gfzrAlVixw0e8VtbusR5DkH3ZcSAbiWiZwqcwlHAmaEBJjV.")
        .roles("USER").build();

    return new InMemoryUserDetailsManager(kash, ken, shimo, akina, tsuna, Z2762, Z2789, Z2819, Z2911, Z2946, Z2974,
        Z3052, Z3055, Z3065, Z3069, Z3182, Z4272, 本田, 福安, 井垣, Z2883, Z3193);
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
