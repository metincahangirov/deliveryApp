package com.example.ai_service_ms.api;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai_service_ms.api.dto.AssistantChatRequest;
import com.example.ai_service_ms.api.dto.AssistantChatResponse;
import com.example.ai_service_ms.security.FoodDeliveryPrincipal;
import com.example.ai_service_ms.service.FoodDeliveryAssistantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/assistant")
@Validated
public class AssistantController {

	private final FoodDeliveryAssistantService assistantService;

	public AssistantController(FoodDeliveryAssistantService assistantService) {
		this.assistantService = assistantService;
	}

	@Operation(
			summary = "Ask the food-delivery AI assistant (OpenAI-compatible LLM: Groq, Ollama, etc.)",
			security = @SecurityRequirement(name = "bearer-jwt")
	)
	@PostMapping("/chat")
	public AssistantChatResponse chat(@Valid @RequestBody AssistantChatRequest request) {
		FoodDeliveryPrincipal principal = (FoodDeliveryPrincipal) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
		String answer = assistantService.answer(principal.userId(), request.message());
		return new AssistantChatResponse(principal.userId(), answer);
	}
}
