package com.example.SlotlyV2.feature.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponse {

    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long expiresIn;

}
