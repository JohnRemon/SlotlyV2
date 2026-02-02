package com.example.SlotlyV2.feature.custom_form.dto;

import java.util.List;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormResponseDTO {

    @Valid
    private List<FieldResponseDTO> fieldAnswers;
}
