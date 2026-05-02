package com.example.ai_service_ms.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuItem(
		UUID menuItemId,
		UUID restaurantId,
		String name,
		FoodCategory category,
		BigDecimal unitPriceEur,
		int platformOrdersLast90Days
) {
}
