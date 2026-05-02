package com.example.ai_service_ms.llm.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatCompletionRequestDto(
		String model,
		List<ChatMessageDto> messages,
		Double temperature
) {
}
