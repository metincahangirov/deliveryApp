package com.example.chatservice_ms.order;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chat.order-service")
public record OrderServiceProperties(String baseUrl) {
}

