package com.example.SlotlyV2.feature.booking_form.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldAnswerDTO {
    @NotNull(message = "field id is required")
    private UUID fieldId;
    private String fieldResponse;
}
