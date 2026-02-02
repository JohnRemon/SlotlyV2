package com.example.SlotlyV2.feature.custom_form.dto.request;

public class UpdateFormRequest {

    @NotEmpty(message = "questions are required")
    @Valid
    private List<FormQuestionDTO> questions;
}
