package com.deliveryapp.userservice.controller;

import com.deliveryapp.userservice.dto.SuccessDto;
import com.deliveryapp.userservice.dto.request.RegisterRequest;
import com.deliveryapp.userservice.dto.response.UserResponse;
import com.deliveryapp.userservice.repository.UserRepository;
import com.deliveryapp.userservice.service.UserService;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-users")
    public ResponseEntity<SuccessDto<List<UserResponse>>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers();

        SuccessDto<List<UserResponse>> response =
                new SuccessDto<>("Success", users);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessDto<UserResponse>> getUserById(@PathVariable UUID id) {

        UserResponse user = userService.getUserById(id);

        SuccessDto<UserResponse> response =
                new SuccessDto<>("Success", user);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessDto<UserResponse>> updateUser(@PathVariable UUID id,
                                                               @RequestBody RegisterRequest request) {
        UserResponse updatedUser = userService.updateUser(id, request);

        SuccessDto<UserResponse> response = new SuccessDto<>("Success", updatedUser);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessDto<UserResponse>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
