package com.example.SlotlyV2.dto;

import lombok.Data;

@Data
public class UserVerificationDTO {
    private final String displayName;
    private final String email;
    private final String token;
}
