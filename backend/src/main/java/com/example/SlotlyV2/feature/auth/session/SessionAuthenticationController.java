package com.example.SlotlyV2.feature.auth.session;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.DataResponse;
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
    public DataResponse<UserResponse> login(
            @Valid @RequestBody SessionLoginRequest request,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        rateLimitHelper.checkLoginRateLimit(httpServletRequest);

        User user = sessionAuthenticationService.login(request, httpServletRequest, httpServletResponse);

        return DataResponse.of(new UserResponse(user));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        sessionAuthenticationService.logout(request, response);

    }

    @GetMapping("/me")
    public DataResponse<UserResponse> me() {
        User user = sessionAuthenticationService.me();

        return DataResponse.of(new UserResponse(user));
    }
}
