package com.deliveryapp.userservice.service;

import com.deliveryapp.userservice.dto.request.LoginRequest;
import com.deliveryapp.userservice.dto.request.RegisterRequest;
import com.deliveryapp.userservice.dto.response.UserResponse;
import com.deliveryapp.userservice.repository.UserRepository;


import java.util.List;
import java.util.UUID;

public interface UserService {


    List<UserResponse> getAllUsers();

    UserResponse getUserById(UUID id);

    UserResponse updateUser(UUID id, RegisterRequest request);

    void deleteUser(UUID id);

    void createAdmin(RegisterRequest request);
}
