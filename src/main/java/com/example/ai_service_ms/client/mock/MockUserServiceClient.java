package com.example.ai_service_ms.client.mock;

import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.example.ai_service_ms.client.UserServiceClient;
import com.example.ai_service_ms.client.support.ClientResponseMapper;
import com.example.ai_service_ms.client.support.DeliveryFixtures;
import com.example.ai_service_ms.domain.UserDinerProfile;

@Service
@ConditionalOnProperty(name = "app.integration.mode", havingValue = "mock", matchIfMissing = true)
public class MockUserServiceClient implements UserServiceClient {

	@Override
	public UserDinerProfile getDinerProfile(UUID userId, String authorizationHeader) {
		return ClientResponseMapper.toProfile(DeliveryFixtures.profileFor(userId));
	}
}
