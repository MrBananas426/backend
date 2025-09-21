package com.example.icebreaker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * OAuth2LoginConfig
 *
 * No hard-coded secrets. Values are injected from application.properties,
 * which should map to environment variables:
 *   GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET
 *
 * This preserves a minimal public API (getters) so existing code that
 * autowires this config and reads credentials continues to work.
 */
@Configuration
public class OAuth2LoginConfig {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")      // ← HIGHLIGHT
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")  // ← HIGHLIGHT
    private String googleClientSecret;

    public String getGoogleClientId() {                                          // ← HIGHLIGHT
        return googleClientId;
    }

    public String getGoogleClientSecret() {                                      // ← HIGHLIGHT
        return googleClientSecret;
    }
}
