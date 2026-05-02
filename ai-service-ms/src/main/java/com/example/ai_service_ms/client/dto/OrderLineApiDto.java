package com.example.ai_service_ms.client.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderLineApiDto(UUID menuItemId, String category, int quantity) {
}
