package com.example.notificationservice_ms.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateNotificationRequest(
        @NotNull(message = "userId mütləqdir.")
        UUID userId,
        @NotBlank(message = "title boş ola bilməz.")
        @Size(max = 200)
        String title,
        @NotBlank(message = "message boş ola bilməz.")
        @Size(max = 2000)
        String message
) {
}
