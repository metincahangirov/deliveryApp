package com.example.chatservice_ms.chat;

import com.example.chatservice_ms.auth.AuthenticatedUser;
import com.example.chatservice_ms.auth.UserRole;
import com.example.chatservice_ms.chat.dto.ChatEventPayload;
import com.example.chatservice_ms.chat.dto.ChatMessageResponse;
import com.example.chatservice_ms.chat.dto.OrderChatHistoryResponse;
import com.example.chatservice_ms.chat.dto.SendMessageRequest;
import com.example.chatservice_ms.common.ApiException;
import com.example.chatservice_ms.common.UuidStrings;
import com.example.chatservice_ms.order.OrderChatContext;
import com.example.chatservice_ms.order.OrderServiceClient;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository repository;
    private final OrderServiceClient orderServiceClient;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(ChatMessageRepository repository,
                       OrderServiceClient orderServiceClient,
                       SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.orderServiceClient = orderServiceClient;
        this.messagingTemplate = messagingTemplate;
    }

    public ChatMessageResponse sendMessage(AuthenticatedUser sender, SendMessageRequest request, String authHeader) {
        String orderId = UuidStrings.normalizeRequired(request.orderId(), "orderId");
        OrderChatContext order = orderServiceClient.getOrderChatContext(orderId, authHeader);
        ensureAccess(sender, order);

        String receiverId = resolveReceiverId(sender, order);
        ChatMessage message = new ChatMessage();
        message.setOrderId(orderId);
        message.setSenderId(sender.userId());
        message.setReceiverId(receiverId);
        message.setContent(request.content().trim());
        ChatMessage saved = repository.save(message);

        ChatMessageResponse response = ChatMessageResponse.from(saved);
        messagingTemplate.convertAndSendToUser(
                receiverId,
                "/queue/messages",
                new ChatEventPayload("chat.message.created", response)
        );
        return response;
    }

    public OrderChatHistoryResponse getOrderMessages(String orderId, AuthenticatedUser requester, String authHeader) {
        String canonicalOrderId = UuidStrings.normalizeRequired(orderId, "orderId");
        OrderChatContext order = orderServiceClient.getOrderChatContext(canonicalOrderId, authHeader);
        ensureAccess(requester, order);

        List<ChatMessageResponse> messages = repository.findByOrderIdOrderByCreatedAtAsc(canonicalOrderId)
                .stream()
                .map(ChatMessageResponse::from)
                .toList();

        return new OrderChatHistoryResponse(canonicalOrderId, messages);
    }

    private void ensureAccess(AuthenticatedUser user, OrderChatContext order) {
        if (user.role() == UserRole.USER && user.userId().equals(order.userId())) {
            return;
        }
        if (user.role() == UserRole.COURIER && user.userId().equals(order.courierId())) {
            return;
        }
        throw new ApiException(HttpStatus.FORBIDDEN, "CHAT_ACCESS_DENIED", "You are not allowed to access this order chat.");
    }

    private String resolveReceiverId(AuthenticatedUser sender, OrderChatContext order) {
        if (sender.role() == UserRole.USER) {
            return order.courierId();
        }
        if (sender.role() == UserRole.COURIER) {
            return order.userId();
        }
        throw new ApiException(HttpStatus.FORBIDDEN, "CHAT_ACCESS_DENIED", "Unsupported sender role.");
    }
}

