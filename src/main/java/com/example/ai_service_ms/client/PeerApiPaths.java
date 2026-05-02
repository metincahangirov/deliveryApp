package com.example.ai_service_ms.client;

import java.util.UUID;

/**
 * Internal REST paths expected from the platform's User, Order, and Restaurant services.
 */
public final class PeerApiPaths {

	private PeerApiPaths() {
	}

	public static String dinerProfile(UUID userId) {
		return "/api/v1/internal/diners/" + userId + "/profile";
	}

	public static String dinerDeliveredOrders(UUID userId) {
		return "/api/v1/internal/diners/" + userId + "/orders?status=DELIVERED&limit=100";
	}

	public static String zoneRestaurantCatalog(UUID deliveryZoneId) {
		return "/api/v1/internal/delivery-zones/" + deliveryZoneId + "/restaurants-with-menus";
	}
}
