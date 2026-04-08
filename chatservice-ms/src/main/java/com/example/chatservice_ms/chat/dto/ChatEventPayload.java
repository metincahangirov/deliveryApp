package com.example.chatservice_ms.chat.dto;

public record ChatEventPayload(
        String eventType,
        ChatMessageResponse data
) {
}

