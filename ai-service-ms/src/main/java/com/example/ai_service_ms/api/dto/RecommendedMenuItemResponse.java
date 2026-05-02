package com.example.ai_service_ms.api.dto;

import java.util.List;
import java.util.UUID;

import com.example.ai_service_ms.domain.FoodCategory;

public record RecommendedMenuItemResponse(
		UUID menuItemId,
		UUID restaurantId,
		String name,
		FoodCategory category,
		double score,
		List<String> reasons
) {
}
