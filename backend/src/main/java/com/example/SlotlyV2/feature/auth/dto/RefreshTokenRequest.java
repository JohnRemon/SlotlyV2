package com.example.SlotlyV2.feature.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

}
