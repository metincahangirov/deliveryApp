package com.example.ai_service_ms.client.mock;

import java.util.List;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.example.ai_service_ms.client.OrderServiceClient;
import com.example.ai_service_ms.client.support.ClientResponseMapper;
import com.example.ai_service_ms.client.support.DeliveryFixtures;
import com.example.ai_service_ms.domain.HistoricalOrder;

@Service
@ConditionalOnProperty(name = "app.integration.mode", havingValue = "mock", matchIfMissing = true)
public class MockOrderServiceClient implements OrderServiceClient {

	@Override
	public List<HistoricalOrder> getDeliveredOrders(UUID userId, String authorizationHeader) {
		var payload = userId.equals(DeliveryFixtures.DEMO_DINER_ID)
				? DeliveryFixtures.richOrderHistory()
				: DeliveryFixtures.sparseOrderHistory();
		return ClientResponseMapper.toOrders(payload);
	}
}
