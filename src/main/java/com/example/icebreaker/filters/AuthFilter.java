package com.example.icebreaker.filters;
//
//import com.example.icebreaker.models.Account;
//import com.example.icebreaker.repositories.AccountRepo;
//import com.example.icebreaker.repositories.SessionRepo;
//import com.example.icebreaker.services.TokenService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.AllArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Objects;
//
//@Component
//@AllArgsConstructor
//public class AuthFilter extends OncePerRequestFilter {
//
//
//    private final TokenService tokenService;
//    private final AccountRepo accountRepo;
//    private final SessionRepo sessionRepo;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        System.out.println(request.getRequestURI());
//        System.out.println(request.getContextPath()+" "+request.getServletPath());
//
//if (request.getServletPath().equals("/signing")||request.getServletPath().equals("/signup")) {
//filterChain.doFilter(request, response);
//
//} else {
//
//
//    String token = null;
//    if (request.getCookies() != null) {
//        for (Cookie cookie : request.getCookies()) {
//            if ("token".equals(cookie.getName())) {
//                token = cookie.getValue();
//                break;
//            }
//        }
//    }
//
//    String userId = null;
//    if (request.getCookies() != null) {
//        for (Cookie cookie : request.getCookies()) {
//            if ("id".equals(cookie.getName())) {
//                userId = cookie.getValue();
//                break;
//            }
//        }
//    }
//
//    // Example: Print token
//    if (token != null) {
//        System.out.println("Token found: " + token + " and:" + userId);
//
//        Account account = accountRepo.findByUserId(userId).get();
//
////
//        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
//                account,
//                null,
//                account.getAuthorities()
//        );
//
//        SecurityContextHolder.getContext().setAuthentication(auth);
//
//        filterChain.doFilter(request, response);
//
//    } else {
//
//        System.out.println("No token found");
//        filterChain.doFilter(request, response);
//    }
//
//}
//
//    }
//}

