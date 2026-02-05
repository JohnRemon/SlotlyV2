package com.example.SlotlyV2.feature.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PasswordResetRequest {
    @Email(message = "Please provide a valid email")
    @NotBlank(message = "Email is required")
    private final String email;
}
