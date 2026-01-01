package com.example.SlotlyV2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.dto.ApiResponse;
import com.example.SlotlyV2.dto.LoginRequest;
import com.example.SlotlyV2.dto.PasswordResetConfirmRequest;
import com.example.SlotlyV2.dto.PasswordResetRequest;
import com.example.SlotlyV2.dto.RegisterRequest;
import com.example.SlotlyV2.dto.UserResponse;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.service.UserService;
import com.example.SlotlyV2.service.VerificationTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final VerificationTokenService verificationTokenService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(request);
        return new ApiResponse<>("User registered successfully. Please check your email to verify your account.",
                new UserResponse(user));
    }

    @PostMapping("/login")
    public ApiResponse<UserResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        User user = userService.loginUser(request);
        return new ApiResponse<>("Logged in successfully", new UserResponse(user));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logoutUser(HttpServletRequest request) {
        userService.logout(request);
        return new ApiResponse<>("Logged out successfully", null);
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> userProfile() {
        User user = userService.getCurrentUser();
        return new ApiResponse<>("User fetched successfully", new UserResponse(user));
    }

    @PostMapping("/verify-email")
    public ApiResponse<Void> verifyEmail(@RequestParam String token) {
        verificationTokenService.verifyVerificationToken(token);
        return new ApiResponse<>("Account verified successfully. Please login into your account", null);
    }

    @PostMapping("/reset-password/request")
    public ApiResponse<Void> resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        userService.resetPasswordRequest(request);
        return new ApiResponse<>("An email has been sent to your inbox", null);
    }

    @PostMapping("/reset-password/confirm")
    public ApiResponse<Void> verifyPassword(@RequestParam String token,
            @RequestBody @Valid PasswordResetConfirmRequest request) {
        userService.resetPassword(token, request);
        return new ApiResponse<>("Password changed successfully. Please login", null);
    }
}
