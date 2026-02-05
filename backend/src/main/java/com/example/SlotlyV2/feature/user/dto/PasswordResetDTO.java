package com.example.SlotlyV2.feature.user.dto;

import lombok.Value;

@Value
public class PasswordResetDTO {
    private final String displayName;
    private final String email;
    private final String token;
}
