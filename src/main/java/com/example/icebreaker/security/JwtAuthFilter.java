package com.example.icebreaker.security;

import org.springframework.beans.factory.annotation.Qualifier;   // ← HIGHLIGHT
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;
import java.util.Set;

/**
 * JWT authentication filter:
 * - Skips public endpoints and CORS preflight.
 * - Parses Authorization: Bearer <token>.
 * - If valid, loads the user and sets the SecurityContext.
 *
 * Expects a JwtUtil with: boolean isValid(String) and String getSubject(String).
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;

  public JwtAuthFilter(JwtUtil jwtUtil,
                     @Qualifier("inMemoryUserDetailsService") UserDetailsService userDetailsService) {  // ← HIGHLIGHT
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
}

  // Basic allowlist; expand as needed
  private static final Set<String> PUBLIC_PATHS = Set.of(
    "/login",
    "/signup",
    "/api/auth/login"   // ← HIGHLIGHT: allow your API login path
);

  private boolean isWhitelisted(HttpServletRequest request) {
    // Always allow CORS preflight
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

    String path = request.getRequestURI();
    // Allow any /actuator/** and /h2-console/**
    if (path.startsWith("/actuator")) return true;
    if (path.startsWith("/h2-console")) return true;

    return PUBLIC_PATHS.contains(path);
  }

  @Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    String p = request.getServletPath();
    return p.equals("/login")
        || p.equals("/signup")
        || p.startsWith("/auth/login")
        || p.startsWith("/auth/signup")// if you also have /auth/login, /auth/signup
        || p.startsWith("/actuator")
        || p.startsWith("/h2-console");
}


  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain chain
  ) throws ServletException, IOException {

    if (isWhitelisted(request)) {
      chain.doFilter(request, response);
      return;
    }

    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);

      try {
        if (jwtUtil.isValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
          String subject = jwtUtil.getSubject(token); // e.g., email or userId
          UserDetails user = userDetailsService.loadUserByUsername(subject);

          UsernamePasswordAuthenticationToken auth =
              new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
          auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (Exception ex) {
        // Optional: log this; we clear context and continue
        SecurityContextHolder.clearContext();
      }
    }

    chain.doFilter(request, response);
  }
}
