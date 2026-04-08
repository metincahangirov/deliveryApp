package com.example.chatservice_ms.chat;

import com.example.chatservice_ms.auth.AuthenticatedUser;
import com.example.chatservice_ms.auth.JwtAuthService;
import com.example.chatservice_ms.chat.dto.ChatMessageResponse;
import com.example.chatservice_ms.chat.dto.OrderChatHistoryResponse;
import com.example.chatservice_ms.chat.dto.SendMessageRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final JwtAuthService jwtAuthService;
    private final ChatService chatService;

    public ChatController(JwtAuthService jwtAuthService, ChatService chatService) {
        this.jwtAuthService = jwtAuthService;
        this.chatService = chatService;
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody SendMessageRequest request
    ) {
        AuthenticatedUser sender = jwtAuthService.authenticateBearerHeader(authorization);
        ChatMessageResponse response = chatService.sendMessage(sender, request, authorization);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderChatHistoryResponse> getOrderHistory(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String orderId
    ) {
        AuthenticatedUser requester = jwtAuthService.authenticateBearerHeader(authorization);
        OrderChatHistoryResponse response = chatService.getOrderMessages(orderId, requester, authorization);
        return ResponseEntity.ok(response);
    }
}

