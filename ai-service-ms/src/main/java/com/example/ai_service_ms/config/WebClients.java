package com.example.ai_service_ms.config;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.ai_service_ms.config.ApplicationProperties.RestTimeouts;

import reactor.netty.http.client.HttpClient;

public final class WebClients {

	private WebClients() {
	}

	public static WebClient create(String baseUrl, RestTimeouts timeouts) {
		HttpClient httpClient = HttpClient.create()
				.responseTimeout(Duration.ofMillis(timeouts.readTimeoutMs()));
		return WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.baseUrl(baseUrl)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}
}
