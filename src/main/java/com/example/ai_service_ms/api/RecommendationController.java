package com.example.ai_service_ms.api;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai_service_ms.api.dto.RecommendationResponse;
import com.example.ai_service_ms.service.RecommendationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/recommend")
public class RecommendationController {

	private final RecommendationService recommendationService;

	public RecommendationController(RecommendationService recommendationService) {
		this.recommendationService = recommendationService;
	}

	@Operation(
			summary = "Personalized menu recommendations for the authenticated diner",
			security = @SecurityRequirement(name = "bearer-jwt")
	)
	@GetMapping
	public RecommendationResponse recommend(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
	) {
		return recommendationService.recommendForAuthenticatedDiner(authorization);
	}
}
