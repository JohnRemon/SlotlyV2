package com.example.SlotlyV2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @NotBlank(message = "email is required")
    private String email;
}
