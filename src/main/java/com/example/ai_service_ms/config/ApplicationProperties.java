package com.example.ai_service_ms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.integration")
public record ApplicationProperties(
		IntegrationMode mode,
		String userServiceBaseUrl,
		String orderServiceBaseUrl,
		String restaurantServiceBaseUrl,
		RestTimeouts rest
) {
	public enum IntegrationMode {
		mock,
		live
	}

	public record RestTimeouts(int connectTimeoutMs, int readTimeoutMs) {
	}
}
