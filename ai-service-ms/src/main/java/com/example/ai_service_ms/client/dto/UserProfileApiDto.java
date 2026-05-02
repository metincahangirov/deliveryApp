package com.example.ai_service_ms.client.dto;

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserProfileApiDto(UUID userId, UUID primaryDeliveryZoneId, Set<String> dietaryTags) {
}
