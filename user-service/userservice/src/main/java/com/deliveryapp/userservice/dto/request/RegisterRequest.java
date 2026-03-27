package com.deliveryapp.userservice.dto.request;

public record RegisterRequest(String fullName,
                              String username,
                              String email,
                              String phoneNumber,
                              String password
                              ) {
}
