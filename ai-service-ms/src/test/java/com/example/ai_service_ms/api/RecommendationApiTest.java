package com.example.ai_service_ms.api;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import com.example.ai_service_ms.client.support.DeliveryFixtures;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
class RecommendationApiTest {

	private static final String TEST_SECRET = "01234567890123456789012345678901";

	@Autowired
	private MockMvc mockMvc;

	@Test
	void recommend_withoutAuthorization_returns401() throws Exception {
		mockMvc.perform(get("/recommend").contextPath("/ai-service-ms"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void recommend_withValidJwt_returnsPersonalizedCatalog() throws Exception {
		String jwt = mintJwt(DeliveryFixtures.DEMO_DINER_ID);
		mockMvc.perform(get("/recommend")
				.contextPath("/ai-service-ms")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").value(DeliveryFixtures.DEMO_DINER_ID.toString()))
				.andExpect(jsonPath("$.recommendations.length()").value(greaterThan(0)));
	}

	private static String mintJwt(UUID subject) {
		SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
		return Jwts.builder().subject(subject.toString()).signWith(key).compact();
	}
}
