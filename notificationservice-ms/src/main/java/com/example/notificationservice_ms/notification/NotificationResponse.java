package com.example.notificationservice_ms.notification;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID userId,
        String title,
        String message,
        boolean read,
        Instant createdAt
) {
    static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getUserId(),
                n.getTitle(),
                n.getMessage(),
                n.isReadFlag(),
                n.getCreatedAt()
        );
    }
}
