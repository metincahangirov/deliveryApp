package com.example.ai_service_ms.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI recommendationOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("AI Recommendation Service")
						.description("Food delivery personalization API (JWT-authenticated diners). "
								+ "The subject claim must contain the diner UUID.")
						.version("v1"))
				.components(new Components().addSecuritySchemes(
						"bearer-jwt",
						new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")
				));
	}
}
