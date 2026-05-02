package com.example.ai_service_ms.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.ai_service_ms.api.dto.RecommendationResponse;
import com.example.ai_service_ms.client.OrderServiceClient;
import com.example.ai_service_ms.client.RestaurantServiceClient;
import com.example.ai_service_ms.client.UserServiceClient;
import com.example.ai_service_ms.domain.HistoricalOrder;
import com.example.ai_service_ms.domain.Restaurant;
import com.example.ai_service_ms.domain.UserDinerProfile;
import com.example.ai_service_ms.security.FoodDeliveryPrincipal;

@Service
public class RecommendationService {

	private final UserServiceClient userServiceClient;
	private final OrderServiceClient orderServiceClient;
	private final RestaurantServiceClient restaurantServiceClient;
	private final FoodRecommendationEngine recommendationEngine;

	public RecommendationService(
			UserServiceClient userServiceClient,
			OrderServiceClient orderServiceClient,
			RestaurantServiceClient restaurantServiceClient,
			FoodRecommendationEngine recommendationEngine
	) {
		this.userServiceClient = userServiceClient;
		this.orderServiceClient = orderServiceClient;
		this.restaurantServiceClient = restaurantServiceClient;
		this.recommendationEngine = recommendationEngine;
	}

	public RecommendationResponse recommendForAuthenticatedDiner(String authorizationHeader) {
		FoodDeliveryPrincipal principal = (FoodDeliveryPrincipal) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
		UUID userId = principal.userId();

		UserDinerProfile profile = userServiceClient.getDinerProfile(userId, authorizationHeader);
		if (!profile.userId().equals(userId)) {
			throw new IllegalStateException("User Service returned a profile for a different diner");
		}

		List<HistoricalOrder> orders = orderServiceClient.getDeliveredOrders(userId, authorizationHeader);
		List<Restaurant> restaurants = restaurantServiceClient.getRestaurantsWithMenusForZone(
				profile.primaryDeliveryZoneId(),
				authorizationHeader
		);

		return recommendationEngine.recommend(userId, profile, orders, restaurants);
	}
}
