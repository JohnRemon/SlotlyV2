package com.example.SlotlyV2.feature.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.common.rate_limiting.RateLimitHelper;
import com.example.SlotlyV2.feature.auth.dto.GoogleLoginRequest;
import com.example.SlotlyV2.feature.auth.dto.JwtAuthenticationResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class GoogleOAuthController {
    private final GoogleOAuthService googleOAuthService;
    private final RateLimitHelper rateLimitHelper;

    @PostMapping("/google")
    public ApiResponse<JwtAuthenticationResponse> login(@Valid @RequestBody GoogleLoginRequest request,
            HttpServletRequest httpServletRequest) {
        rateLimitHelper.checkLoginRateLimit(httpServletRequest);

        JwtAuthenticationResponse response = googleOAuthService.login(request.getIdToken());
        return new ApiResponse<>("User logged in successfully via Google", response);
    }
}
