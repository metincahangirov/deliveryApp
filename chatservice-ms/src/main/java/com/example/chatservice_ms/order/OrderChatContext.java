package com.example.chatservice_ms.order;

public record OrderChatContext(
        String orderId,
        String userId,
        String courierId
) {
}

