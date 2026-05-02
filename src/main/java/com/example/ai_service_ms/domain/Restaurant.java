package com.example.ai_service_ms.domain;

import java.util.List;
import java.util.UUID;

public record Restaurant(UUID restaurantId, UUID deliveryZoneId, String name, List<MenuItem> menu) {
}
