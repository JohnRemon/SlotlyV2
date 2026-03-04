package com.example.SlotlyV2.feature.auth.session;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.common.rate_limiting.RateLimitHelper;
import com.example.SlotlyV2.feature.auth.dto.SessionLoginRequest;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.dto.UserResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class SessionAuthenticationController {
    private final SessionAuthenticationService sessionAuthenticationService;
    private final RateLimitHelper rateLimitHelper;

    @PostMapping("/login")
    public ApiResponse<UserResponse> login(@RequestBody @Valid SessionLoginRequest sessionLoginRequest,
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        rateLimitHelper.checkLoginRateLimit(httpServletRequest);
        User user = sessionAuthenticationService.login(sessionLoginRequest, httpServletRequest, httpServletResponse);
        return new ApiResponse<>("Logged in successfully", new UserResponse(user));

    }
}
