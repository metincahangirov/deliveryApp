package com.example.ai_service_ms.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.ai_service_ms.client.support.DeliveryFixtures;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
class AssistantApiTest {

	private static final String TEST_SECRET = "01234567890123456789012345678901";

	@Autowired
	private MockMvc mockMvc;

	@Test
	void chat_whenLlmDisabled_returns503() throws Exception {
		String jwt = mintJwt(DeliveryFixtures.DEMO_DINER_ID);
		mockMvc.perform(post("/assistant/chat")
				.contextPath("/ai-service-ms")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"message\":\"What vegetarian options are popular?\"}"))
				.andExpect(status().isServiceUnavailable())
				.andExpect(jsonPath("$.error").value("LLM_NOT_AVAILABLE"));
	}

	private static String mintJwt(UUID subject) {
		SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
		return Jwts.builder().subject(subject.toString()).signWith(key).compact();
	}
}
