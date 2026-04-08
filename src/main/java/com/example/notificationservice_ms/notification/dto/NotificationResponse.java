package com.example.notificationservice_ms.notification.dto;

import com.example.notificationservice_ms.notification.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID userId,
        String title,
        String body,
        NotificationType type,
        boolean isRead,
        LocalDateTime createdAt
) {
}
