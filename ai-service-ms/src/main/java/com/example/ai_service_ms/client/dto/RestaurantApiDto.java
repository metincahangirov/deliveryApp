package com.example.ai_service_ms.client.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RestaurantApiDto(UUID restaurantId, UUID deliveryZoneId, String name, List<MenuItemApiDto> menu) {
}
