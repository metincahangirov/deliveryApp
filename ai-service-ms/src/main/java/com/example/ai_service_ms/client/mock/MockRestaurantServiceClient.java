package com.example.ai_service_ms.client.mock;

import java.util.List;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.example.ai_service_ms.client.RestaurantServiceClient;
import com.example.ai_service_ms.client.support.ClientResponseMapper;
import com.example.ai_service_ms.client.support.DeliveryFixtures;
import com.example.ai_service_ms.domain.Restaurant;

@Service
@ConditionalOnProperty(name = "app.integration.mode", havingValue = "mock", matchIfMissing = true)
public class MockRestaurantServiceClient implements RestaurantServiceClient {

	@Override
	public List<Restaurant> getRestaurantsWithMenusForZone(UUID deliveryZoneId, String authorizationHeader) {
		if (!deliveryZoneId.equals(DeliveryFixtures.ZONE_HELSINKI_CENTRAL)) {
			return List.of();
		}
		return ClientResponseMapper.toRestaurants(DeliveryFixtures.helsinkiCatalog());
	}
}
