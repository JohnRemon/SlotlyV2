package com.example.SlotlyV2.feature.auth.oauth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.DataResponse;
import com.example.SlotlyV2.common.rate_limiting.RateLimitHelper;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.dto.UserResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class GoogleOAuthController {
    private final GoogleOAuthService googleOAuthService;
    private final RateLimitHelper rateLimitHelper;

    @PostMapping("/google")
    public DataResponse<UserResponse> login(
            @RequestParam String idToken,
            @RequestParam String timeZone,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        rateLimitHelper.checkLoginRateLimit(httpServletRequest);

        User user = googleOAuthService.login(
                idToken,
                timeZone,
                httpServletRequest,
                httpServletResponse);

        return DataResponse.of(new UserResponse(user));
    }
}
