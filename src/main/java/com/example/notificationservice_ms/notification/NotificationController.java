package com.example.notificationservice_ms.notification;

import com.example.notificationservice_ms.notification.dto.CreateNotificationRequest;
import com.example.notificationservice_ms.notification.dto.NotificationResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final String internalToken;

    public NotificationController(
            NotificationService notificationService,
            @Value("${app.security.internal-token:change-me}") String internalToken
    ) {
        this.notificationService = notificationService;
        this.internalToken = internalToken;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @RequestHeader("X-Internal-Token") String headerToken,
            @Valid @RequestBody CreateNotificationRequest request
    ) {
        if (!internalToken.equals(headerToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Internal token yanlisdir.");
        }
        NotificationResponse created = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/me")
    public List<NotificationResponse> getMyNotifications(@AuthenticationPrincipal UUID userId) {
        return notificationService.getMyNotifications(userId);
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationResponse markAsRead(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal UUID userId
    ) {
        return notificationService.markAsRead(notificationId, userId);
    }
}
