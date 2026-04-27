package com.example.notificationservice_ms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record AppJwtProperties(
        String secret,
        long expirationMs
) {
}
