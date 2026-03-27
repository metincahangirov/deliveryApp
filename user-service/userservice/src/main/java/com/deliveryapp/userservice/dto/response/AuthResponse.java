package com.deliveryapp.userservice.dto.response;

public record AuthResponse(
        String accesToken,
        String refreshToken,
        String type,
        String username,
        String role
) {
}
