package com.example.ai_service_ms.client;

import java.util.List;
import java.util.UUID;

import com.example.ai_service_ms.domain.HistoricalOrder;

public interface OrderServiceClient {

	List<HistoricalOrder> getDeliveredOrders(UUID userId, String authorizationHeader);
}
