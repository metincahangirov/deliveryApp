package com.deliveryapp.userservice.service.impl;


import com.deliveryapp.userservice.dto.request.RegisterRequest;
import com.deliveryapp.userservice.dto.response.UserResponse;
import com.deliveryapp.userservice.entity.Role;
import com.deliveryapp.userservice.entity.Status;
import com.deliveryapp.userservice.entity.UserEntity;
import com.deliveryapp.userservice.repository.UserRepository;
import com.deliveryapp.userservice.service.UserService;
import jdk.jshell.spi.ExecutionControl;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.deliveryapp.userservice.mapper.UserMapper;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }



    @Override
    public List<UserResponse> getAllUsers() {

        List<UserEntity> userEntities = userRepository.findAll();
        return userMapper.mapEntityListToResponseList(userEntities);
    }

    @Override
    public UserResponse getUserById(UUID id) {

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponse(
                userEntity.getId(),
                userEntity.getFullName(),
                userEntity.getUsername(),
                userEntity.getEmail()
        );


    }

    @Override
    public UserResponse updateUser(UUID id, RegisterRequest registerRequest) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(registerRequest.fullName());
        user.setEmail(registerRequest.email());
        user.setPhoneNumber(registerRequest.phoneNumber());
        user.setUsername(registerRequest.username());

        user.setPassword(passwordEncoder.encode(registerRequest.password()));

        UserEntity updated = userRepository.save(user);

        return userMapper.mapEntityToResponse(updated);
    }

    @Override
    public void deleteUser(UUID id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }

    @Override
    public void createAdmin(RegisterRequest request) {

        UserEntity user = new UserEntity();

        user.setUsername(request.fullName());
        user.setEmail(request.email());
        user.setFullName(request.fullName());

        user.setPassword(passwordEncoder.encode(request.password()));

        user.setRole(Role.ADMIN);

        userRepository.save(user);
    }
}
