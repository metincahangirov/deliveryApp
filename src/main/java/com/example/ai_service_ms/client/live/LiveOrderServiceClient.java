package com.example.ai_service_ms.client.live;

import java.util.List;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.ai_service_ms.client.PeerApiPaths;
import com.example.ai_service_ms.client.PeerServiceException;
import com.example.ai_service_ms.client.OrderServiceClient;
import com.example.ai_service_ms.client.dto.OrderApiDto;
import com.example.ai_service_ms.client.support.ClientResponseMapper;
import com.example.ai_service_ms.config.ApplicationProperties;
import com.example.ai_service_ms.config.WebClients;
import com.example.ai_service_ms.domain.HistoricalOrder;

import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty(name = "app.integration.mode", havingValue = "live")
public class LiveOrderServiceClient implements OrderServiceClient {

	private final WebClient webClient;

	public LiveOrderServiceClient(ApplicationProperties properties) {
		this.webClient = WebClients.create(properties.orderServiceBaseUrl(), properties.rest());
	}

	@Override
	@Cacheable(cacheNames = "orderHistory", key = "#userId.toString()")
	public List<HistoricalOrder> getDeliveredOrders(UUID userId, String authorizationHeader) {
		List<OrderApiDto> orders = webClient.get()
				.uri(PeerApiPaths.dinerDeliveredOrders(userId))
				.header(HttpHeaders.AUTHORIZATION, authorizationHeader)
				.retrieve()
				.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
						.defaultIfEmpty("")
						.flatMap(body -> Mono.error(new PeerServiceException(
								"Order Service error " + response.statusCode() + " for " + userId + ": " + body
						))))
				.bodyToMono(new ParameterizedTypeReference<List<OrderApiDto>>() {
				})
				.block();
		if (orders == null) {
			return List.of();
		}
		return ClientResponseMapper.toOrders(orders);
	}
}
