package com.example.ai_service_ms.domain;

import java.util.List;
import java.util.UUID;

public record HistoricalOrder(UUID orderId, UUID restaurantId, List<OrderLine> lines) {
}
