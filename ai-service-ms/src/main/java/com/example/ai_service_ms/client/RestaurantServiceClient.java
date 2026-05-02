package com.example.ai_service_ms.client;

import java.util.List;
import java.util.UUID;

import com.example.ai_service_ms.domain.Restaurant;

public interface RestaurantServiceClient {

	List<Restaurant> getRestaurantsWithMenusForZone(UUID deliveryZoneId, String authorizationHeader);
}
