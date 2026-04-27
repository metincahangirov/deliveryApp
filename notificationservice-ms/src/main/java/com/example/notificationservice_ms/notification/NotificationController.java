package com.example.notificationservice_ms.notification;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse create(@Valid @RequestBody CreateNotificationRequest request) {
        return notificationService.create(request);
    }

    @GetMapping("/me")
    public List<NotificationResponse> myNotifications(Authentication authentication) {
        UUID userId = requireUserId(authentication);
        return notificationService.listForUser(userId);
    }

    @GetMapping("/me/{id}")
    public NotificationResponse myNotification(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        UUID userId = requireUserId(authentication);
        return notificationService.getForUser(id, userId);
    }

    @PatchMapping("/me/{id}/read")
    public NotificationResponse markRead(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        UUID userId = requireUserId(authentication);
        return notificationService.markRead(id, userId);
    }

    private static UUID requireUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UUID userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT ilə daxil olun.");
        }
        return userId;
    }
}
