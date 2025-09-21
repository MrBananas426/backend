package com.example.icebreaker.config;

import com.example.icebreaker.security.JwtAuthFilter;                         // ← add
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // ← add

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authProvider,
            TokenBasedRememberMeServices rememberMeServices,
            JwtAuthFilter jwtAuthFilter                                        // ← add
    ) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .authenticationProvider(authProvider)
            .rememberMe(rm -> rm.rememberMeServices(rememberMeServices))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // ← add
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }


    @Bean
@Qualifier("inMemoryUserDetailsService")
public UserDetailsService inMemoryUserDetailsService(PasswordEncoder encoder) {
    return new InMemoryUserDetailsManager(
        User.withUsername("demo@example.com")
            .password(encoder.encode("Passw0rd!"))
            // Provide BOTH forms so @PreAuthorize with hasRole('USER') or hasAuthority('USER') passes
            .authorities("ROLE_USER", "USER")
            .build()
    );
}


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            @Qualifier("inMemoryUserDetailsService") UserDetailsService uds,
            PasswordEncoder encoder) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(encoder);
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider provider) {
        return new ProviderManager(provider);
    }

    @Bean
    public TokenBasedRememberMeServices rememberMeServices(
            @Qualifier("inMemoryUserDetailsService") UserDetailsService uds) {
        String key = System.getenv().getOrDefault("REMEMBER_ME_KEY", "change-me-now");
        TokenBasedRememberMeServices s = new TokenBasedRememberMeServices(key, uds);
        s.setParameter("remember-me");
        s.setCookieName("remember-me");
        return s;
    }
}
