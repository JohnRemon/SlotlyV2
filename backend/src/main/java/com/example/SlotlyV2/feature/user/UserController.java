package com.example.SlotlyV2.feature.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.DataResponse;
import com.example.SlotlyV2.common.rate_limiting.RateLimitHelper;
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
    private final RateLimitHelper rateLimitHelper;

    @GetMapping("/me")
    public DataResponse<UserResponse> me() {
        User user = userService.getCurrentUser();
        return DataResponse.of(new UserResponse(user));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse<UserResponse> registerUser(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpServletRequest) {
        rateLimitHelper.checkRegisterRateLimit(httpServletRequest);
        return DataResponse.of(userService.registerUser(request));
    }

    @PatchMapping("/{id}/first-name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFirstName(@PathVariable Long id, @RequestParam String firstName) {
        userService.updateFirstName(id, firstName);
    }

    @PatchMapping("/{id}/last-name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLastName(@PathVariable Long id, @RequestParam String lastName) {
        userService.updateLastName(id, lastName);
    }

    @PatchMapping("/timezone")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTimezone(@PathVariable Long id, @RequestParam String timeZone) {
        userService.updateTimeZone(id, timeZone);
    }
}
