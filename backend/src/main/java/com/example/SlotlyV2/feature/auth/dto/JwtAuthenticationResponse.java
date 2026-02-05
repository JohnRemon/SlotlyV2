package com.example.SlotlyV2.feature.auth.dto;

import com.example.SlotlyV2.feature.user.dto.UserResponse;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class JwtAuthenticationResponse {

    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserResponse user;
}
