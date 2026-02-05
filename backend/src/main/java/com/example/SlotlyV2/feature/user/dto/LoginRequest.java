package com.example.SlotlyV2.feature.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class LoginRequest {
    @Email
    @NotBlank(message = "Email is required")
    private final String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password size must be greater than 8")
    private final String password;
}
