package com.example.SlotlyV2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.dto.ApiResponse;
import com.example.SlotlyV2.dto.PasswordResetConfirmRequest;
import com.example.SlotlyV2.dto.LoginRequest;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final VerificationTokenService verificationTokenService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(request);

        ApiResponse<UserResponse> response = new ApiResponse<>(
                "User registered successfully. Please check your email to verify your account.",
                new UserResponse(user));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> loginUser(@Valid @RequestBody LoginRequest request) {
        User user = userService.loginUser(request);

        ApiResponse<UserResponse> response = new ApiResponse<>("Logged in successfully", new UserResponse(user));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logoutUser(HttpServletRequest request) {
        userService.logout(request);

        ApiResponse<Void> response = new ApiResponse<Void>("Logged out successfully", null);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> userProfile() {
        User user = userService.getCurrentUser();

        ApiResponse<UserResponse> response = new ApiResponse<>("User fetched successfully", new UserResponse(user));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
        verificationTokenService.verifyVerificationToken(token);

        ApiResponse<Void> response = new ApiResponse<>(
                "Account verified successfully. Please login into your account",
                null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody PasswordResetRequest request) {
        userService.resetPasswordRequest(request);

        ApiResponse<Void> response = new ApiResponse<Void>("An email has been sent to your inbox", null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<ApiResponse<Void>> verifyPassword(@RequestParam String token,
            @RequestBody PasswordResetConfirmRequest request) {
        userService.resetPassword(token, request);

        ApiResponse<Void> response = new ApiResponse<Void>("Password changed successfully. Please login", null);

        return ResponseEntity.ok(response);
    }

    // So the user first sends a request with only his email to reset-password
    // We send the user an email with password Token
    // then we send him to /reset-password?token=<token> and he can send a post
    // request there with his password and its confirmation
    // the user password is changed successfully
}
