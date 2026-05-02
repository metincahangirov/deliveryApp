package com.example.ai_service_ms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Validated
@ConfigurationProperties(prefix = "app.llm")
public record LlmProperties(
		boolean enabled,
		@NotBlank String baseUrl,
		@NotBlank String model,
		@Positive int readTimeoutMs,
		String apiKey,
		String systemPrompt
) {
}
