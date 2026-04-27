package com.example.notificationservice_ms.auth;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresInMs
) {
}
