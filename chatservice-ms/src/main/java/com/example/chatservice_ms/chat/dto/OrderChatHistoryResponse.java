package com.example.chatservice_ms.chat.dto;

import java.util.List;

public record OrderChatHistoryResponse(
        String orderId,
        List<ChatMessageResponse> messages
) {
}

