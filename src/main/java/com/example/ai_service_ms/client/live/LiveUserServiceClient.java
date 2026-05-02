package com.example.ai_service_ms.client.live;

import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.ai_service_ms.client.PeerApiPaths;
import com.example.ai_service_ms.client.PeerServiceException;
import com.example.ai_service_ms.client.UserServiceClient;
import com.example.ai_service_ms.client.dto.UserProfileApiDto;
import com.example.ai_service_ms.client.support.ClientResponseMapper;
import com.example.ai_service_ms.config.ApplicationProperties;
import com.example.ai_service_ms.config.WebClients;
import com.example.ai_service_ms.domain.UserDinerProfile;

import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty(name = "app.integration.mode", havingValue = "live")
public class LiveUserServiceClient implements UserServiceClient {

	private final WebClient webClient;

	public LiveUserServiceClient(ApplicationProperties properties) {
		this.webClient = WebClients.create(properties.userServiceBaseUrl(), properties.rest());
	}

	@Override
	@Cacheable(cacheNames = "dinerProfiles", key = "#userId.toString()")
	public UserDinerProfile getDinerProfile(UUID userId, String authorizationHeader) {
		UserProfileApiDto dto = webClient.get()
				.uri(PeerApiPaths.dinerProfile(userId))
				.header(HttpHeaders.AUTHORIZATION, authorizationHeader)
				.retrieve()
				.onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
						.defaultIfEmpty("")
						.flatMap(body -> Mono.error(new PeerServiceException(
								"User Service error " + response.statusCode() + " for " + userId + ": " + body
						))))
				.bodyToMono(UserProfileApiDto.class)
				.block();
		if (dto == null) {
			throw new PeerServiceException("User Service returned empty profile for " + userId);
		}
		return ClientResponseMapper.toProfile(dto);
	}
}
