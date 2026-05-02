package com.example.ai_service_ms.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.ai_service_ms.config.LlmProperties;
import com.example.ai_service_ms.llm.LlmException;
import com.example.ai_service_ms.llm.OpenAiCompatibleLlmClient;
import com.example.ai_service_ms.llm.dto.ChatMessageDto;

@Service
public class FoodDeliveryAssistantService {

	private static final String DEFAULT_SYSTEM_PROMPT = """
			You are a helpful, concise assistant for a food delivery app (similar to Wolt or Uber Eats).
			Answer only about: ordering food, restaurants, menus, delivery times, fees, refunds, \
			dietary preferences, and account help related to food delivery.
			If the user asks for anything outside this domain, reply in one short sentence that you \
			can only help with food delivery topics.
			Keep answers short unless the user explicitly asks for detail.""";

	private final LlmProperties llmProperties;
	private final OpenAiCompatibleLlmClient llmClient;

	public FoodDeliveryAssistantService(LlmProperties llmProperties, OpenAiCompatibleLlmClient llmClient) {
		this.llmProperties = llmProperties;
		this.llmClient = llmClient;
	}

	public String answer(UUID authenticatedUserId, String userMessage) {
		if (!llmProperties.enabled()) {
			throw new LlmException(
					"LLM assistant is disabled. Set app.llm.enabled=true and configure LLM_BASE_URL / GROQ_API_KEY "
							+ "(Groq free tier) or use local Ollama (LLM_BASE_URL=http://localhost:11434/v1, no API key)."
			);
		}

		String base = llmProperties.baseUrl().toLowerCase();
		boolean likelyGroq = base.contains("groq.com");
		if (likelyGroq && (llmProperties.apiKey() == null || llmProperties.apiKey().isBlank())) {
			throw new LlmException(
					"Groq requires an API key. Create a free key at https://console.groq.com/ and set GROQ_API_KEY."
			);
		}

		String system = llmProperties.systemPrompt();
		if (system == null || system.isBlank()) {
			system = DEFAULT_SYSTEM_PROMPT;
		}

		system = system + "\nThe authenticated diner userId (UUID) is: " + authenticatedUserId
				+ ". Do not invent orders or personal data; if needed, tell the user to check the app.";

		List<ChatMessageDto> messages = List.of(
				new ChatMessageDto("system", system),
				new ChatMessageDto("user", userMessage)
		);

		return llmClient.chatCompletion(messages);
	}
}
