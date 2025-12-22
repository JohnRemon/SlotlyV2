package com.example.SlotlyV2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.dto.ApiResponse;
import com.example.SlotlyV2.dto.LoginRequest;
import com.example.SlotlyV2.dto.UserRegistrationRequest;
import com.example.SlotlyV2.dto.UserResponse;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        User user = userService.registerUser(
                request.getEmail(),
                request.getUsername(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getTimeZone());

        ApiResponse<UserResponse> response = new ApiResponse<>("User registered successfully", new UserResponse(user));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> loginUser(@Valid @RequestBody LoginRequest request) {
        User user = userService.loginUser(
                request.getEmail(),
                request.getPassword());

        ApiResponse<UserResponse> response = new ApiResponse<>("Logged in successfully", new UserResponse(user));

        return ResponseEntity.ok(response);
    }
}
