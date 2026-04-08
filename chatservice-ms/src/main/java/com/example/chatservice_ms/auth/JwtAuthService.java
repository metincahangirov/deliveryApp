package com.example.chatservice_ms.auth;

import com.example.chatservice_ms.common.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

@Service
public class JwtAuthService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final byte[] secretKeyBytes;

    public JwtAuthService(JwtProperties properties) {
        String secret = properties.resolvedSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is missing. Configure app.security.jwt-secret (or app.security.internal-token).");
        }
        this.secretKeyBytes = secret.getBytes(StandardCharsets.UTF_8);
    }

    public AuthenticatedUser authenticateBearerHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Missing or invalid Authorization header.");
        }

        String token = authorizationHeader.substring(7);
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Invalid token format.");
            }

            verifyHs256Signature(parts[0], parts[1], parts[2]);
            JsonNode payload = decodePayload(parts[1]);

            String userId = textValue(payload, "sub");
            String roleValue = textValue(payload, "role");
            if (userId == null || userId.isBlank() || roleValue == null || roleValue.isBlank()) {
                throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Token claims are missing.");
            }

            validateExpiration(payload);
            UserRole role = UserRole.valueOf(roleValue.toUpperCase());
            return new AuthenticatedUser(userId, role);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Invalid token.");
        }
    }

    private void verifyHs256Signature(String encodedHeader, String encodedPayload, String encodedSignature) throws Exception {
        String data = encodedHeader + "." + encodedPayload;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secretKeyBytes, "HmacSHA256"));
        byte[] expected = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        byte[] provided = Base64.getUrlDecoder().decode(encodedSignature);

        if (!MessageDigest.isEqual(expected, provided)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Invalid token signature.");
        }
    }

    private JsonNode decodePayload(String encodedPayload) throws Exception {
        byte[] payloadBytes = Base64.getUrlDecoder().decode(encodedPayload);
        return OBJECT_MAPPER.readTree(payloadBytes);
    }

    private String textValue(JsonNode payload, String fieldName) {
        JsonNode node = payload.get(fieldName);
        return node == null || node.isNull() ? null : node.asText();
    }

    private void validateExpiration(JsonNode payload) {
        JsonNode expNode = payload.get("exp");
        if (expNode == null || expNode.isNull()) {
            return;
        }
        long exp = expNode.asLong();
        if (Instant.now().getEpochSecond() >= exp) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Token expired.");
        }
    }
}

