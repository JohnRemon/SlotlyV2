package com.example.SlotlyV2.feature.password_reset;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        rateLimitHelper.checkPasswordResetRateLimit(request.getEmail());

        passwordResetService.resetPasswordRequest(request);
    }

    @PostMapping("/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyPassword(@RequestParam String token,
            @RequestBody @Valid PasswordResetConfirmRequest request, HttpServletRequest httpServletRequest) {
        passwordResetService.resetPassword(token, request);
    }
}
