package com.example.chatservice_ms.chat.dto;

import com.example.chatservice_ms.common.UuidStrings;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(
        @NotBlank(message = "orderId is required")
        @Pattern(regexp = UuidStrings.UUID_REGEX, message = "orderId must be a valid UUID")
        String orderId,
        @NotBlank(message = "content is required")
        @Size(max = 2000, message = "content must not exceed 2000 characters")
        String content
) {
}

