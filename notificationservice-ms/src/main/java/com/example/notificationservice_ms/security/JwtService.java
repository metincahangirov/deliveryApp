package com.example.notificationservice_ms.security;

import com.example.notificationservice_ms.config.AppJwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final AppJwtProperties properties;
    private final SecretKey signingKey;

    public JwtService(AppJwtProperties properties) {
        this.properties = properties;
        byte[] keyBytes = properties.secret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret must be at least 32 bytes (UTF-8) for HS256.");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(UUID userId) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(properties.expirationMs());
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(signingKey)
                .compact();
    }

    public UUID parseUserId(String bearerToken) {
        String token = stripBearer(bearerToken);
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return UUID.fromString(claims.getSubject());
    }

    private static String stripBearer(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("JWT token boşdur.");
        }
        String t = raw.trim();
        if (t.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return t.substring(7).trim();
        }
        return t;
    }
}
