package com.example.ai_service_ms.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderHistoryApiDto(List<OrderApiDto> orders) {
}
