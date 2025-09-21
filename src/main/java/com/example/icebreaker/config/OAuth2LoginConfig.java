package com.example.icebreaker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2LoginConfig {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    public String getGoogleClientId() { return googleClientId; }

    public String getGoogleClientSecret() { return googleClientSecret; }
}