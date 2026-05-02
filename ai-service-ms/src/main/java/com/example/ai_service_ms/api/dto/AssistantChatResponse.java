package com.example.ai_service_ms.api.dto;

import java.util.UUID;

public record AssistantChatResponse(UUID userId, String answer) {
}
