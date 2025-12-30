package com.example.SlotlyV2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetConfirmRequest {
    @NotBlank(message = "Password is required")
    private final String password;
    @NotBlank(message = "Confirm your password")
    private final String confirmPassword;
}
