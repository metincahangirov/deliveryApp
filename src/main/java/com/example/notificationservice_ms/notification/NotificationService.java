package com.example.notificationservice_ms.notification;

import com.example.notificationservice_ms.notification.dto.CreateNotificationRequest;
import com.example.notificationservice_ms.notification.dto.NotificationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.userId());
        notification.setType(request.type());
        notification.setTitle(resolveTitle(request));
        notification.setBody(resolveBody(request));
        notification.setRead(false);

        Notification saved = notificationRepository.save(notification);
        return NotificationMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }

    @Transactional
    public NotificationResponse markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification tapilmadi."));

        notification.setRead(true);
        return NotificationMapper.toResponse(notification);
    }

    private String resolveTitle(CreateNotificationRequest request) {
        if (request.title() != null && !request.title().isBlank()) {
            return request.title().trim();
        }
        return NotificationMessageBuilder.resolveTitle(request.type());
    }

    private String resolveBody(CreateNotificationRequest request) {
        if (request.body() != null && !request.body().isBlank()) {
            return request.body().trim();
        }
        return NotificationMessageBuilder.resolveBody(request.type());
    }
}
