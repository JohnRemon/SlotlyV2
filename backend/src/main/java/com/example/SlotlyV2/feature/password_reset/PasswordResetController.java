package com.example.SlotlyV2.feature.password_reset;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.common.rate_limiting.RateLimitHelper;
import com.example.SlotlyV2.feature.user.dto.PasswordResetConfirmRequest;
import com.example.SlotlyV2.feature.user.dto.PasswordResetRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/password-reset")
@RequiredArgsConstructor
public class PasswordResetController {
    private final PasswordResetService passwordResetService;
    private final RateLimitHelper rateLimitHelper;

    @PostMapping("/request")
    public ApiResponse<Void> resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        rateLimitHelper.checkPasswordResetRateLimit(request.getEmail());

        passwordResetService.resetPasswordRequest(request);
        return new ApiResponse<>("An email has been sent to your inbox", null);
    }

    @PostMapping("/confirm")
    public ApiResponse<Void> verifyPassword(@RequestParam String token,
            @RequestBody @Valid PasswordResetConfirmRequest request, HttpServletRequest httpServletRequest) {
        passwordResetService.resetPassword(token, request);
        return new ApiResponse<>("Password changed successfully. Please login", null);
    }
}
