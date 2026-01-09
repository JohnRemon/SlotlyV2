package com.example.SlotlyV2.feature.auth.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RefreshTokenResponse {

    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long expiresIn;

}
