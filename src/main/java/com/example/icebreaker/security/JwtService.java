// C:\Users\codyv\Desktop\backend\backend\src\main\java\com\example\icebreaker\security\JwtService.java
package com.example.icebreaker.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;                                  // ← HIGHLIGHT
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    // ← HIGHLIGHT: use a raw UTF-8 secret (not Base64); 32+ chars for HS256
    private static final String DEV_SECRET = "demo-secret-should-be-at-least-32-chars-long-123";

    // ← HIGHLIGHT: token lifetime (adjust if needed)
    private static final long EXP_MILLIS = 1000L * 60 * 60 * 24 * 7; // 7 days

    // ↓↓↓ HIGHLIGHT: no Base64 decode; use raw bytes with Keys.hmacShaKeyFor
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(DEV_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ↓↓↓ HIGHLIGHT: simple generator used by AuthController
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
                .setExpiration(new Date(now + EXP_MILLIS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)      // ← HIGHLIGHT
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
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return exp.before(new Date());
    }
}
