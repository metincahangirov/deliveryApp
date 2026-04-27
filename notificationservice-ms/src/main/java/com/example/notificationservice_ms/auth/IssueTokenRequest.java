package com.example.notificationservice_ms.auth;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record IssueTokenRequest(
        @NotNull(message = "userId mütləqdir.")
        UUID userId
) {
}
