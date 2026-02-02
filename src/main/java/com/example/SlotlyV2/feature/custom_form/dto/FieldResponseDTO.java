package com.example.SlotlyV2.feature.custom_form.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldResponseDTO {
    private UUID fieldId;
    private String fieldResponse;
}
