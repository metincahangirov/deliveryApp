package com.example.chatservice_ms.chat.dto;

import com.example.chatservice_ms.chat.ChatMessage;

import java.time.Instant;

public record ChatMessageResponse(
        String id,
        String orderId,
        String senderId,
        String receiverId,
        String content,
        Instant createdAt
) {
    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getOrderId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}

