package com.example.ai_service_ms.api.dto;

import java.util.List;
import java.util.UUID;

public record RecommendationResponse(UUID userId, List<RecommendedMenuItemResponse> recommendations) {
}
