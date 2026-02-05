package com.example.SlotlyV2.feature.calendar.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExchangeRequest {
    @NotBlank(message = "Authorization code is required")
    private String code;

    @NotBlank(message = "State is required")
    private String state;
}
