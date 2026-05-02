package com.example.ai_service_ms.security;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenValidator {

	private final JwtProperties properties;
	private final SecretKey secretKey;

	public JwtTokenValidator(JwtProperties properties) {
		this.properties = properties;
		byte[] keyBytes = properties.secret().getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length < 32) {
			throw new IllegalStateException("JWT secret must be at least 256 bits (32 bytes) for HS256");
		}
		this.secretKey = Keys.hmacShaKeyFor(keyBytes);
	}

	public Claims parseAndValidate(String rawJwt) {
		try {
			return Jwts.parser()
					.verifyWith(secretKey)
					.build()
					.parseSignedClaims(rawJwt)
					.getPayload();
		}
		catch (JwtException ex) {
			throw new InvalidJwtException("Invalid or expired JWT", ex);
		}
	}

	public UUID extractUserId(String rawJwt) {
		Claims claims = parseAndValidate(rawJwt);
		String subject = claims.get(properties.subjectClaim(), String.class);
		if (subject == null || subject.isBlank()) {
			throw new InvalidJwtException("JWT missing subject (" + properties.subjectClaim() + ")");
		}
		try {
			return UUID.fromString(subject);
		}
		catch (IllegalArgumentException ex) {
			throw new InvalidJwtException("JWT subject is not a UUID", ex);
		}
	}

	public static String stripBearerPrefix(String authorizationHeader) {
		if (authorizationHeader == null || authorizationHeader.isBlank()) {
			throw new InvalidJwtException("Missing Authorization header");
		}
		String prefix = "Bearer ";
		if (!authorizationHeader.regionMatches(true, 0, prefix, 0, prefix.length())) {
			throw new InvalidJwtException("Authorization must be a Bearer token");
		}
		return authorizationHeader.substring(prefix.length()).trim();
	}

	public static final class InvalidJwtException extends RuntimeException {
		public InvalidJwtException(String message) {
			super(message);
		}

		public InvalidJwtException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
