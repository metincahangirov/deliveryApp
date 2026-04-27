package com.example.chatservice_ms.common;

import com.example.chatservice_ms.order.OrderChatContext;
import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Bütün ictimai identifikatorlar (JWT sub, orderId, istifadəçi/kuryer id-ləri) UUID formatındadır.
 */
public final class UuidStrings {

    /** Jakarta {@code @Pattern} / path param validasiyası üçün (RFC-4122 string). */
    public static final String UUID_REGEX =
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    private UuidStrings() {
    }

    public static String requireUuidSubClaim(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Token claims are missing.");
        }
        try {
            return UUID.fromString(raw.trim()).toString();
        } catch (IllegalArgumentException ex) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Token subject (sub) must be a valid UUID.");
        }
    }

    /** API body/query/path üçün: yanlış formatda {@code 400 INVALID_UUID}. */
    public static String normalizeRequired(String raw, String fieldName) {
        if (raw == null || raw.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_UUID", fieldName + " must be a non-blank UUID.");
        }
        try {
            return UUID.fromString(raw.trim()).toString();
        } catch (IllegalArgumentException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_UUID", fieldName + " must be a valid UUID.");
        }
    }

    /** Order Service cavabındakı id-ləri kanonik UUID stringinə çevirir. */
    public static OrderChatContext normalizeOrderChatContext(OrderChatContext ctx) {
        try {
            return new OrderChatContext(
                    UUID.fromString(ctx.orderId().trim()).toString(),
                    UUID.fromString(ctx.userId().trim()).toString(),
                    UUID.fromString(ctx.courierId().trim()).toString()
            );
        } catch (IllegalArgumentException ex) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "ORDER_SERVICE_INVALID_IDS",
                    "Order service returned identifiers that are not valid UUIDs.");
        }
    }
}
