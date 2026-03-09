package com.example.SlotlyV2.feature.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.DataResponse;
import com.example.SlotlyV2.common.rate_limiting.RateLimitHelper;
import com.example.SlotlyV2.feature.auth.VerificationTokenService;
import com.example.SlotlyV2.feature.user.dto.RegisterRequest;
import com.example.SlotlyV2.feature.user.dto.UserResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final RateLimitHelper rateLimitHelper;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse<UserResponse> registerUser(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpServletRequest) {

        rateLimitHelper.checkRegisterRateLimit(httpServletRequest);
        User user = userService.registerUser(request);
        return DataResponse.of(new UserResponse(user));
    }

    @PostMapping("/verify-email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyEmail(@RequestParam String token) {
        verificationTokenService.verifyEmailVerificationToken(token);
    }
}
