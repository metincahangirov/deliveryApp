package com.example.ai_service_ms.llm.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatCompletionResponseDto(List<ChoiceDto> choices) {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ChoiceDto(MessageDto message) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record MessageDto(String role, String content) {
	}
}
