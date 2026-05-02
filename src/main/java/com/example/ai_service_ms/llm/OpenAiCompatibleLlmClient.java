package com.example.ai_service_ms.llm;

import java.time.Duration;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.ai_service_ms.config.LlmProperties;
import com.example.ai_service_ms.llm.dto.ChatCompletionRequestDto;
import com.example.ai_service_ms.llm.dto.ChatCompletionResponseDto;
import com.example.ai_service_ms.llm.dto.ChatMessageDto;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Component
public class OpenAiCompatibleLlmClient {

	private final WebClient webClient;
	private final LlmProperties properties;

	public OpenAiCompatibleLlmClient(LlmProperties properties) {
		this.properties = properties;
		String base = properties.baseUrl().replaceAll("/+$", "");
		HttpClient httpClient = HttpClient.create()
				.responseTimeout(Duration.ofMillis(properties.readTimeoutMs()));
		this.webClient = WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.baseUrl(base)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}

	public String chatCompletion(List<ChatMessageDto> messages) {
		var request = new ChatCompletionRequestDto(
				properties.model(),
				messages,
				0.7
		);

		WebClient.RequestBodySpec bodySpec = webClient.post()
				.uri("/chat/completions")
				.contentType(MediaType.APPLICATION_JSON);

		String key = properties.apiKey();
		if (key != null && !key.isBlank()) {
			bodySpec = bodySpec.header(HttpHeaders.AUTHORIZATION, "Bearer " + key.trim());
		}

		ChatCompletionResponseDto response = bodySpec
				.bodyValue(request)
				.retrieve()
				.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
						.defaultIfEmpty("")
						.flatMap(body -> Mono.error(new LlmException(
								"LLM HTTP " + clientResponse.statusCode().value() + ": " + body
						))))
				.bodyToMono(ChatCompletionResponseDto.class)
				.block();

		if (response == null || response.choices() == null || response.choices().isEmpty()) {
			throw new LlmException("LLM returned an empty response");
		}
		var message = response.choices().getFirst().message();
		if (message == null || message.content() == null || message.content().isBlank()) {
			throw new LlmException("LLM returned empty assistant content");
		}
		return message.content().trim();
	}
}
