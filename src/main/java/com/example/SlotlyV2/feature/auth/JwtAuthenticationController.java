package com.example.SlotlyV2.feature.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.feature.auth.dto.JwtAuthenticationResponse;
import com.example.SlotlyV2.feature.auth.dto.JwtLoginRequest;
import com.example.SlotlyV2.feature.auth.dto.RefreshTokenRequest;
import com.example.SlotlyV2.feature.auth.dto.RefreshTokenResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth/jwt")
@RequiredArgsConstructor
public class JwtAuthenticationController {

    private final JwtAuthenticationService jwtAuthenticationService;

    @PostMapping("/login")
    public ApiResponse<JwtAuthenticationResponse> login(@Valid @RequestBody JwtLoginRequest request) {
        JwtAuthenticationResponse authenticationResponse = jwtAuthenticationService.login(request);
        return new ApiResponse<>("User logged in successfully using JWT", authenticationResponse);
    }

    @PostMapping("/refresh")
    public ApiResponse<RefreshTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse refreshTokenResponse = jwtAuthenticationService.refresh(request);
        return new ApiResponse<>("Token refreshed successfully", refreshTokenResponse);
    }
}
