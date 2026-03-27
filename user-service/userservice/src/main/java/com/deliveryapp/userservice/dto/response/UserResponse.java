package com.deliveryapp.userservice.dto.response;

import java.util.UUID;


public record UserResponse(UUID uuid,
                           String fullname,
                           String username,
                           String email
                               ) {
}
