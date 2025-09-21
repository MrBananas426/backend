package com.example.icebreaker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;               // ← HIGHLIGHT
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;       // ← HIGHLIGHT
import org.springframework.security.crypto.password.PasswordEncoder;          // ← HIGHLIGHT
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest; // ← HIGHLIGHT

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(h -> h.frameOptions(f -> f.sameOrigin())) // H2 console
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(EndpointRequest.to("health", "info")).permitAll() // ← HIGHLIGHT
                .requestMatchers("/api/auth/**").permitAll()                        // ← HIGHLIGHT
                .requestMatchers("/h2-console/**").permitAll()                      // ← HIGHLIGHT (dev)
                .anyRequest().authenticated()
            )
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // ← HIGHLIGHT
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {                                     // ← HIGHLIGHT
        return new BCryptPasswordEncoder();
    }
}
