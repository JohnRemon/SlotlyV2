package com.example.SlotlyV2.feature.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @Email(message = "Please provide a valid email")
    @NotBlank(message = "Email is required")
    private String email;
}
