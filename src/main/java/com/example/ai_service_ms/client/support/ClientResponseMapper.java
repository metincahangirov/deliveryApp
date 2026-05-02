package com.example.ai_service_ms.client.support;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.ai_service_ms.client.dto.MenuItemApiDto;
import com.example.ai_service_ms.client.dto.OrderApiDto;
import com.example.ai_service_ms.client.dto.OrderLineApiDto;
import com.example.ai_service_ms.client.dto.RestaurantApiDto;
import com.example.ai_service_ms.client.dto.UserProfileApiDto;
import com.example.ai_service_ms.domain.DietaryTag;
import com.example.ai_service_ms.domain.FoodCategory;
import com.example.ai_service_ms.domain.HistoricalOrder;
import com.example.ai_service_ms.domain.MenuItem;
import com.example.ai_service_ms.domain.OrderLine;
import com.example.ai_service_ms.domain.Restaurant;
import com.example.ai_service_ms.domain.UserDinerProfile;

public final class ClientResponseMapper {

	private ClientResponseMapper() {
	}

	public static UserDinerProfile toProfile(UserProfileApiDto dto) {
		Set<DietaryTag> tags = dto.dietaryTags() == null
				? Set.of()
				: dto.dietaryTags().stream()
						.map(ClientResponseMapper::parseDietaryTag)
						.filter(Objects::nonNull)
						.collect(Collectors.toCollection(() -> EnumSet.noneOf(DietaryTag.class)));
		return new UserDinerProfile(dto.userId(), dto.primaryDeliveryZoneId(), tags);
	}

	private static DietaryTag parseDietaryTag(String raw) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		try {
			return DietaryTag.valueOf(raw.trim().toUpperCase());
		}
		catch (IllegalArgumentException ex) {
			return null;
		}
	}

	public static List<HistoricalOrder> toOrders(List<OrderApiDto> orders) {
		return orders.stream().map(ClientResponseMapper::toOrder).toList();
	}

	private static HistoricalOrder toOrder(OrderApiDto order) {
		List<OrderLine> lines = order.lines().stream().map(ClientResponseMapper::toLine).toList();
		return new HistoricalOrder(order.orderId(), order.restaurantId(), lines);
	}

	private static OrderLine toLine(OrderLineApiDto line) {
		return new OrderLine(line.menuItemId(), FoodCategory.fromApiCode(line.category()), line.quantity());
	}

	public static List<Restaurant> toRestaurants(List<RestaurantApiDto> restaurants) {
		return restaurants.stream().map(ClientResponseMapper::toRestaurant).toList();
	}

	private static Restaurant toRestaurant(RestaurantApiDto restaurant) {
		List<MenuItem> menu = restaurant.menu().stream().map(ClientResponseMapper::toMenuItem).toList();
		return new Restaurant(restaurant.restaurantId(), restaurant.deliveryZoneId(), restaurant.name(), menu);
	}

	private static MenuItem toMenuItem(MenuItemApiDto item) {
		int popularity = item.platformOrdersLast90Days() == null ? 0 : item.platformOrdersLast90Days();
		return new MenuItem(
				item.menuItemId(),
				item.restaurantId(),
				item.name(),
				FoodCategory.fromApiCode(item.category()),
				item.unitPriceEur(),
				popularity
		);
	}
}
