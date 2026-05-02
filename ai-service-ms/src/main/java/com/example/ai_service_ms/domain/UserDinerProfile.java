package com.example.ai_service_ms.domain;

import java.util.Set;
import java.util.UUID;

public record UserDinerProfile(
		UUID userId,
		UUID primaryDeliveryZoneId,
		Set<DietaryTag> dietaryTags
) {
}
