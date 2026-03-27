package com.deliveryapp.userservice.service;

import com.deliveryapp.userservice.dto.request.LoginRequest;
import com.deliveryapp.userservice.dto.request.RegisterRequest;
import com.deliveryapp.userservice.dto.response.AuthResponse;

public interface UserAuthService {
    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshToken);

    AuthResponse register(RegisterRequest registerRequest);

}
