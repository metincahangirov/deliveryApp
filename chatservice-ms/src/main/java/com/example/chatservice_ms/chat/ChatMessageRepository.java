package com.example.chatservice_ms.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findByOrderIdOrderByCreatedAtAsc(String orderId);
}

