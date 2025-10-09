// â† HIGHLIGHT: Ensure the very first character in the file is 'p' in 'package' (NO characters before it)
package com.example.icebreaker.security;

import org.springframework.context.annotation.Lazy;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Lazy
@Component
public class JwtAuthFilter extends org.springframework.web.filter.OncePerRequestFilter {
  private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;

  public JwtAuthFilter(
      JwtUtil jwtUtil,
      @Qualifier("customUserDetailsService") UserDetailsService userDetailsService // â† HIGHLIGHT (DB-backed service)
  ) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  private boolean isCorsPreflight(HttpServletRequest request) {
    return "OPTIONS".equalsIgnoreCase(request.getMethod())
        && request.getHeader("Origin") != null
        && request.getHeader("Access-Control-Request-Method") != null;
  }

  private boolean isWhitelisted(HttpServletRequest request) {
    String p = request.getServletPath();
    return p.equals("/login")
        || p.equals("/signup")
        || p.startsWith("/auth/login")
        || p.startsWith("/auth/signup")
        || p.startsWith("/actuator")
        || p.startsWith("/h2-console");
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain chain
  ) throws ServletException, IOException {

    if (isCorsPreflight(request) || isWhitelisted(request)) {
      chain.doFilter(request, response);
      return;
    }

    // --- token extraction (Authorization Bearer | Authorization raw | X-Auth-Token | ?token=) ---
    String token = null;
    String authorization = request.getHeader("Authorization");
    if (authorization != null) {
      if (authorization.regionMatches(true, 0, "Bearer ", 0, 7) && authorization.length() > 7) {
        token = authorization.substring(7).trim();
      } else {
        token = authorization.trim();
      }
    }
    if (token == null || token.isEmpty()) {
      String x = request.getHeader("X-Auth-Token");
      if (x != null && !x.isBlank()) token = x.trim();
    }
    if (token == null || token.isEmpty()) {
      String q = request.getParameter("token");
      if (q != null && !q.isBlank()) token = q.trim();
    }

    if (token == null || token.isEmpty()) {
      log.debug("No token found on {}", request.getRequestURI());
      chain.doFilter(request, response);
      return;
    }

    // --- validate and authenticate ---
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        boolean valid = jwtUtil.isValid(token);
        log.debug("Token present for {} (len={}): valid={}",
            request.getRequestURI(), token.length(), valid);

        if (valid) {
          String subject = jwtUtil.getSubject(token);
          UserDetails user = userDetailsService.loadUserByUsername(subject);
          UsernamePasswordAuthenticationToken auth =
              new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
          auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(auth);
          log.debug("Authenticated subject={} with authorities={}", subject, user.getAuthorities());
        }
      } catch (Exception ex) {
        log.debug("JWT processing failed: {}", ex.getMessage());
        SecurityContextHolder.clearContext();
      }
    }

    chain.doFilter(request, response);
  }
}
