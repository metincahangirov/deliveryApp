package com.example.ai_service_ms.client.support;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.ai_service_ms.client.dto.MenuItemApiDto;
import com.example.ai_service_ms.client.dto.OrderApiDto;
import com.example.ai_service_ms.client.dto.OrderLineApiDto;
import com.example.ai_service_ms.client.dto.RestaurantApiDto;
import com.example.ai_service_ms.client.dto.UserProfileApiDto;

/**
 * Deterministic UUIDs and payloads for mock integration (Wolt-style Helsinki inner-city scenario).
 */
public final class DeliveryFixtures {

	private DeliveryFixtures() {
	}

	public static final UUID ZONE_HELSINKI_CENTRAL = UUID.fromString("7f2d6c1a-9c4b-4e8f-a3d1-2b9c0e5f7a81");

	public static final UUID DEMO_DINER_ID = UUID.fromString("a0f0c4d2-6b1e-4a2b-9d0c-7c0b0e1a2d3f");

	public static final UUID RESTAURANT_PIZZA_NAPOLI = UUID.fromString("11111111-2222-3333-4444-555555551001");
	public static final UUID RESTAURANT_SUSHI_KATA = UUID.fromString("11111111-2222-3333-4444-555555551002");
	public static final UUID RESTAURANT_BURGER_HARBOUR = UUID.fromString("11111111-2222-3333-4444-555555551003");

	public static final UUID MENU_MARGHERITA = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-000000000101");
	public static final UUID MENU_DIAVOLA = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-000000000102");
	public static final UUID MENU_SALMON_NIGIRI = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-000000000201");
	public static final UUID MENU_POKE_BOWL = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-000000000202");
	public static final UUID MENU_CHEESEBURGER = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-000000000301");
	public static final UUID MENU_VEGAN_BURGER = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-000000000302");
	public static final UUID MENU_CAESAR_SALAD = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-000000000401");
	public static final UUID MENU_TIRAMISU = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-000000000501");
	public static final UUID MENU_COLA = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-000000000601");

	public static UserProfileApiDto profileFor(UUID userId) {
		return new UserProfileApiDto(userId, ZONE_HELSINKI_CENTRAL, java.util.Set.of("VEGETARIAN"));
	}

	public static List<OrderApiDto> richOrderHistory() {
		List<OrderApiDto> orders = new ArrayList<>();
		orders.add(new OrderApiDto(
				UUID.fromString("bbbbbbbb-1111-1111-1111-111111111101"),
				RESTAURANT_PIZZA_NAPOLI,
				List.of(
						new OrderLineApiDto(MENU_MARGHERITA, "PIZZA", 2),
						new OrderLineApiDto(MENU_DIAVOLA, "PIZZA", 1)
				)
		));
		orders.add(new OrderApiDto(
				UUID.fromString("bbbbbbbb-1111-1111-1111-111111111102"),
				RESTAURANT_PIZZA_NAPOLI,
				List.of(new OrderLineApiDto(MENU_MARGHERITA, "PIZZA", 1))
		));
		orders.add(new OrderApiDto(
				UUID.fromString("bbbbbbbb-1111-1111-1111-111111111103"),
				RESTAURANT_SUSHI_KATA,
				List.of(
						new OrderLineApiDto(MENU_SALMON_NIGIRI, "SUSHI", 2),
						new OrderLineApiDto(MENU_POKE_BOWL, "POKE", 1)
				)
		));
		orders.add(new OrderApiDto(
				UUID.fromString("bbbbbbbb-1111-1111-1111-111111111104"),
				RESTAURANT_BURGER_HARBOUR,
				List.of(new OrderLineApiDto(MENU_CHEESEBURGER, "BURGER", 1))
		));
		return orders;
	}

	public static List<OrderApiDto> sparseOrderHistory() {
		return List.of(new OrderApiDto(
				UUID.fromString("bbbbbbbb-2222-2222-2222-222222222201"),
				RESTAURANT_SUSHI_KATA,
				List.of(new OrderLineApiDto(MENU_SALMON_NIGIRI, "SUSHI", 1))
		));
	}

	public static List<RestaurantApiDto> helsinkiCatalog() {
		return List.of(
				new RestaurantApiDto(
						RESTAURANT_PIZZA_NAPOLI,
						ZONE_HELSINKI_CENTRAL,
						"Pizza Napoli Kluuvi",
						List.of(
								item(MENU_MARGHERITA, RESTAURANT_PIZZA_NAPOLI, "Margherita", "PIZZA", "12.90", 9800),
								item(MENU_DIAVOLA, RESTAURANT_PIZZA_NAPOLI, "Diavola", "PIZZA", "14.50", 4100)
						)
				),
				new RestaurantApiDto(
						RESTAURANT_SUSHI_KATA,
						ZONE_HELSINKI_CENTRAL,
						"Sushi Kata",
						List.of(
								item(MENU_SALMON_NIGIRI, RESTAURANT_SUSHI_KATA, "Salmon nigiri (8 pcs)", "SUSHI", "13.80", 7600),
								item(MENU_POKE_BOWL, RESTAURANT_SUSHI_KATA, "Spicy salmon poke", "POKE", "15.20", 5200)
						)
				),
				new RestaurantApiDto(
						RESTAURANT_BURGER_HARBOUR,
						ZONE_HELSINKI_CENTRAL,
						"Harbour Burgers",
						List.of(
								item(MENU_CHEESEBURGER, RESTAURANT_BURGER_HARBOUR, "Classic cheeseburger", "BURGER", "11.50", 6900),
								item(MENU_VEGAN_BURGER, RESTAURANT_BURGER_HARBOUR, "Vegan smash burger", "BURGER", "12.90", 2100),
								item(MENU_CAESAR_SALAD, RESTAURANT_BURGER_HARBOUR, "Caesar salad", "SALAD", "10.90", 1800),
								item(MENU_TIRAMISU, RESTAURANT_BURGER_HARBOUR, "Tiramisu slice", "DESSERT", "6.50", 4300),
								item(MENU_COLA, RESTAURANT_BURGER_HARBOUR, "Cola 0.33L", "DRINK", "3.50", 12500)
						)
				)
		);
	}

	private static MenuItemApiDto item(
			UUID menuItemId,
			UUID restaurantId,
			String name,
			String category,
			String price,
			int platformOrdersLast90Days
	) {
		return new MenuItemApiDto(
				menuItemId,
				restaurantId,
				name,
				category,
				new BigDecimal(price),
				platformOrdersLast90Days
		);
	}
}
