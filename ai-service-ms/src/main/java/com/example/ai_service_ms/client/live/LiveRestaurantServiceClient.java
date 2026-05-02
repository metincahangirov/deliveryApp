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
import com.example.ai_service_ms.client.RestaurantServiceClient;
import com.example.ai_service_ms.client.dto.RestaurantApiDto;
import com.example.ai_service_ms.client.support.ClientResponseMapper;
import com.example.ai_service_ms.config.ApplicationProperties;
import com.example.ai_service_ms.config.WebClients;
import com.example.ai_service_ms.domain.Restaurant;

import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty(name = "app.integration.mode", havingValue = "live")
public class LiveRestaurantServiceClient implements RestaurantServiceClient {

	private final WebClient webClient;

	public LiveRestaurantServiceClient(ApplicationProperties properties) {
		this.webClient = WebClients.create(properties.restaurantServiceBaseUrl(), properties.rest());
	}

	@Override
	@Cacheable(cacheNames = "zoneCatalogs", key = "#deliveryZoneId.toString()")
	public List<Restaurant> getRestaurantsWithMenusForZone(UUID deliveryZoneId, String authorizationHeader) {
		List<RestaurantApiDto> restaurants = webClient.get()
				.uri(PeerApiPaths.zoneRestaurantCatalog(deliveryZoneId))
				.header(HttpHeaders.AUTHORIZATION, authorizationHeader)
				.retrieve()
				.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
						.defaultIfEmpty("")
						.flatMap(body -> Mono.error(new PeerServiceException(
								"Restaurant Service error " + response.statusCode()
										+ " for zone " + deliveryZoneId + ": " + body
						))))
				.bodyToMono(new ParameterizedTypeReference<List<RestaurantApiDto>>() {
				})
				.block();
		if (restaurants == null) {
			return List.of();
		}
		return ClientResponseMapper.toRestaurants(restaurants);
	}
}
