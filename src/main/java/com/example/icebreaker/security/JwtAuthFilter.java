package com.example.icebreaker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthFilter with permissive token extraction:
 * - Supports "Authorization: Bearer <token>"
 * - Supports "Authorization: <token>"
 * - Supports "X-Auth-Token" header
 * - Supports "token" query parameter
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;

  public JwtAuthFilter(
      JwtUtil jwtUtil,
      @Qualifier("inMemoryUserDetailsService") UserDetailsService userDetailsService
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

    // ---- Necessary correction: broader token extraction ----
    String token = null;

    String authorization = request.getHeader("Authorization");
    if (authorization != null) {
      if (authorization.regionMatches(true, 0, "Bearer ", 0, 7) && authorization.length() > 7) {
        token = authorization.substring(7).trim(); // "Bearer <token>"
      } else {
        token = authorization.trim();               // "<token>"
      }
    }

    if (token == null || token.isEmpty()) {
      String xHeader = request.getHeader("X-Auth-Token");
      if (xHeader != null && !xHeader.isBlank()) {
        token = xHeader.trim();
      }
    }

    if (token == null || token.isEmpty()) {
      String q = request.getParameter("token");
      if (q != null && !q.isBlank()) {
        token = q.trim();
      }
    }
    // --------------------------------------------------------

    if (token != null && !token.isEmpty()
        && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        if (jwtUtil.isValid(token)) {
          String subject = jwtUtil.getSubject(token); // e.g., email or userId
          UserDetails user = userDetailsService.loadUserByUsername(subject);

          UsernamePasswordAuthenticationToken auth =
              new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
          auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (Exception ex) {
        SecurityContextHolder.clearContext();
      }
    }

    chain.doFilter(request, response);
  }
}
