package com.example.icebreaker.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;                // ← HIGHLIGHT
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final Key signingKey;                                   // ← HIGHLIGHT
    private final long expMillis;                                   // ← HIGHLIGHT

    public JwtService(
            @Value("${app.jwt.secret}") String secret,              // ← HIGHLIGHT (same prop as JwtUtil)
            @Value("${app.jwt.expiration-ms}") long expMillis) {    // ← HIGHLIGHT
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expMillis = expMillis;
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)                          // ← HIGHLIGHT
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UserDetails user) {
        return buildToken(new HashMap<>(), user.getUsername());
    }

    public String generateToken(String username) {
        return buildToken(new HashMap<>(), username);
    }

    private String buildToken(Map<String, Object> claims, String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expMillis))
                .signWith(signingKey, SignatureAlgorithm.HS256)     // ← HIGHLIGHT
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails user) {
        try {
            String username = extractUsername(token);
            return username.equals(user.getUsername()) && !isExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    private boolean isExpired(String token) {
        Date exp = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return exp.before(new Date());
    }
}
