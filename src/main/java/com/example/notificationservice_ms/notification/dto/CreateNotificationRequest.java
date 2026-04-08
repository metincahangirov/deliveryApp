package com.example.notificationservice_ms.notification.dto;

import com.example.notificationservice_ms.notification.NotificationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateNotificationRequest(
        @NotNull UUID userId,
        @NotNull NotificationType type,
        @Size(max = 255) String title,
        @Size(max = 1000) String body
) {
}
