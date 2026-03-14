package com.example.SlotlyV2.feature.auth.verify_email;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.DataResponse;
import com.example.SlotlyV2.feature.auth.VerificationTokenService;
import com.example.SlotlyV2.feature.auth.session.SessionAuthenticationService;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.dto.UserResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth/verify-email")
@RequiredArgsConstructor
public class VerifyEmailController {
    private final VerificationTokenService verificationTokenService;
    private final VerifyEmailService verifyEmailService;
    private final SessionAuthenticationService sessionAuthenticationService;

    @PostMapping("/confirm")
    public DataResponse<UserResponse> verifyEmail(
            @RequestParam String token,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        User user = verificationTokenService.verifyEmailVerificationToken(token);
        sessionAuthenticationService.login(user, httpServletRequest, httpServletResponse);
        return DataResponse.of(new UserResponse(user));
    }

    @PostMapping("/resend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resendVerificationEmail(@RequestParam String email) {
        verifyEmailService.resendVerificationEmail(email);
    }

}
