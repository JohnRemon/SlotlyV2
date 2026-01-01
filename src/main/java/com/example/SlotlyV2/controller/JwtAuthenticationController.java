package com.example.SlotlyV2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.dto.ApiResponse;
import com.example.SlotlyV2.dto.JwtAuthenticationResponse;
import com.example.SlotlyV2.dto.JwtLoginRequest;
import com.example.SlotlyV2.dto.RefreshTokenRequest;
import com.example.SlotlyV2.dto.RefreshTokenResponse;
import com.example.SlotlyV2.service.JwtAuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth/jwt")
@RequiredArgsConstructor
public class JwtAuthenticationController {

    private final JwtAuthenticationService jwtAuthenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> login(@Valid @RequestBody JwtLoginRequest request) {
        JwtAuthenticationResponse authenticationResponse = jwtAuthenticationService.login(request);

        ApiResponse<JwtAuthenticationResponse> response = new ApiResponse<>("User logged in successfully using JWT",
                authenticationResponse);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse refreshTokenResponse = jwtAuthenticationService.refresh(request);

        ApiResponse<RefreshTokenResponse> response = new ApiResponse<>("Token refreshed successfully",
                refreshTokenResponse);

        return ResponseEntity.ok(response);
    }
}
