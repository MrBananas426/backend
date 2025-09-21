// C:\Users\codyv\Desktop\backend\backend\src\main\java\com\example\icebreaker\config\SecurityConfig.java
package com.example.icebreaker.config;

import com.example.icebreaker.security.JwtAuthFilter;

import org.springframework.beans.factory.annotation.Qualifier;                                    // ← HIGHLIGHT
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;                       // ← HIGHLIGHT
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;              // ← HIGHLIGHT
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;                                       // ← HIGHLIGHT
import org.springframework.security.core.userdetails.UserDetails;                              // ← HIGHLIGHT
import org.springframework.security.core.userdetails.UserDetailsService;                       // ← HIGHLIGHT
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;                     // ← HIGHLIGHT
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;  // ← HIGHLIGHT
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // ↓↓↓ HIGHLIGHT: named in-memory user store for dev login
  @Bean(name = "inMemoryUserDetailsService")
  public UserDetailsService inMemoryUserDetailsService(PasswordEncoder encoder) {
    UserDetails user = User.withUsername("demo@example.com")                 // ← HIGHLIGHT
        .password(encoder.encode("Passw0rd!"))                               // ← HIGHLIGHT
        .roles("USER")
        .build();
    return new InMemoryUserDetailsManager(user);
  }

  // ↓↓↓ HIGHLIGHT: remember-me bean (your AuthService asked for it)
  @Bean
  public TokenBasedRememberMeServices rememberMeServices(
      @Qualifier("inMemoryUserDetailsService") UserDetailsService uds) {     // ← HIGHLIGHT
    TokenBasedRememberMeServices svc =
        new TokenBasedRememberMeServices("dev-remember-me-key", uds);
    svc.setTokenValiditySeconds(60 * 60 * 24 * 14);
    svc.setAlwaysRemember(false);
    svc.setCookieName("remember-me");
    svc.setParameter("remember-me");
    return svc;
  }

  // ↓↓↓ HIGHLIGHT: concrete provider to handle username/password auth
  @Bean
  public AuthenticationProvider daoAuthProvider(
      @Qualifier("inMemoryUserDetailsService") UserDetailsService uds,       // ← HIGHLIGHT
      PasswordEncoder encoder) {
    DaoAuthenticationProvider p = new DaoAuthenticationProvider();
    p.setUserDetailsService(uds);
    p.setPasswordEncoder(encoder);
    return p;
  }

  // ↓↓↓ HIGHLIGHT: accept beans as parameters; no field injection (prevents cycles)
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                 JwtAuthFilter jwtAuthFilter,
                                                 TokenBasedRememberMeServices rememberMeServices,
                                                 AuthenticationProvider daoAuthProvider) throws Exception {
    http
      .csrf(csrf -> csrf.disable())                                          // ← HIGHLIGHT
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
        .requestMatchers("/acuator/health", "/acuator/health/**")
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/error", "/h2-console/**").permitAll()
        .anyRequest().authenticated()
      )
      .authenticationProvider(daoAuthProvider)                                // ← HIGHLIGHT
      .rememberMe(rm -> rm.rememberMeServices(rememberMeServices))
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
      .cors(cors -> {});
    // If you use H2 console, you may also need:
    // http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("*"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(false);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
    return cfg.getAuthenticationManager();
  }
}
