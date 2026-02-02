package com.example.SlotlyV2.feature.custom_form.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFormRequest {
    @NotNull(message = "event ID is required")
    private Long eventId;

    @NotEmpty(message = "questions are required")
    @Valid(message = "questions must be valid")
    private List<FormQuestionDTO> questions;
}
