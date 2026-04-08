package com.example.chatservice_ms.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record JwtProperties(String jwtSecret, String internalToken) {

    public String resolvedSecret() {
        if (jwtSecret != null && !jwtSecret.isBlank()) {
            return jwtSecret;
        }
        if (internalToken != null && !internalToken.isBlank()) {
            return internalToken;
        }
        return null;
    }
}

