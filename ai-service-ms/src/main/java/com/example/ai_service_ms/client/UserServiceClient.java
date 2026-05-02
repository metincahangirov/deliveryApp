package com.example.ai_service_ms.client;

import java.util.UUID;

import com.example.ai_service_ms.domain.UserDinerProfile;

public interface UserServiceClient {

	UserDinerProfile getDinerProfile(UUID userId, String authorizationHeader);
}
