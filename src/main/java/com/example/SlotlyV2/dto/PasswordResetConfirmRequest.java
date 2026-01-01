package com.example.SlotlyV2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetConfirmRequest {
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password size must be greater than 8")
    private final String password;

    @NotBlank(message = "Confirm your password")
    private final String confirmPassword;
}
