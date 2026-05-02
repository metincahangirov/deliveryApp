package com.example.ai_service_ms.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AssistantChatRequest(
		@NotBlank @Size(max = 4000) String message
) {
}
