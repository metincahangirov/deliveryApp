package com.example.ai_service_ms.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MenuItemApiDto(
		UUID menuItemId,
		UUID restaurantId,
		String name,
		String category,
		BigDecimal unitPriceEur,
		Integer platformOrdersLast90Days
) {
}
