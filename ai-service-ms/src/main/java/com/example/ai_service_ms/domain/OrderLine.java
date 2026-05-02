package com.example.ai_service_ms.domain;

import java.util.UUID;

public record OrderLine(UUID menuItemId, FoodCategory category, int quantity) {
}
