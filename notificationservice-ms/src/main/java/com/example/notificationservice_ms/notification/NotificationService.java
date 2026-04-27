package com.example.notificationservice_ms.notification;

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
    public NotificationResponse create(CreateNotificationRequest request) {
        Notification entity = new Notification(request.userId(), request.title(), request.message());
        return NotificationResponse.from(notificationRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listForUser(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificationResponse getForUser(UUID notificationId, UUID userId) {
        Notification n = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new NotificationNotFoundException("Bildiriş tapılmadı."));
        return NotificationResponse.from(n);
    }

    @Transactional
    public NotificationResponse markRead(UUID notificationId, UUID userId) {
        Notification n = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new NotificationNotFoundException("Bildiriş tapılmadı."));
        n.setReadFlag(true);
        return NotificationResponse.from(n);
    }
}
