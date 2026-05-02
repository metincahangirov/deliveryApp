package com.example.ai_service_ms.llm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatMessageDto(String role, String content) {
}
