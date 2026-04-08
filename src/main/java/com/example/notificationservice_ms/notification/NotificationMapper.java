package com.example.notificationservice_ms.notification;

import com.example.notificationservice_ms.notification.dto.NotificationResponse;

final class NotificationMapper {

    private NotificationMapper() {
    }

    static NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUserId(),
                notification.getTitle(),
                notification.getBody(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
