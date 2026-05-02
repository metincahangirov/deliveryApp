package com.example.ai_service_ms.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(String secret, String subjectClaim) {

	public JwtProperties {
		if (subjectClaim == null || subjectClaim.isBlank()) {
			subjectClaim = "sub";
		}
	}
}
