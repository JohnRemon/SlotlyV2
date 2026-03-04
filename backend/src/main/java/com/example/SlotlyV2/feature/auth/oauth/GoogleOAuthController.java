package com.example.SlotlyV2.feature.auth.oauth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.common.rate_limiting.RateLimitHelper;
import com.example.SlotlyV2.feature.auth.dto.GoogleLoginRequest;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.dto.UserResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class GoogleOAuthController {
    private final GoogleOAuthService googleOAuthService;
    private final RateLimitHelper rateLimitHelper;

    @PostMapping("/google")
    public ApiResponse<UserResponse> login(
            @Valid @RequestBody GoogleLoginRequest request,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        rateLimitHelper.checkLoginRateLimit(httpServletRequest);

        User user = googleOAuthService.login(
                request.getIdToken(),
                httpServletRequest,
                httpServletResponse);

        return new ApiResponse<>("Logged in successfully via Google", new UserResponse(user));
    }
}
