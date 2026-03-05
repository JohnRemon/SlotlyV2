package com.example.SlotlyV2.feature.booking_form.dto;

import java.util.UUID;

import com.example.SlotlyV2.feature.booking_form.FieldAnswer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Size(max = 5000, message = "response cannot exceed 5000 characters")
    private String fieldResponse;

    public FieldAnswerDTO(FieldAnswer answer) {
        this.fieldId = answer.getId();
        this.fieldResponse = answer.getAnswer();
    }
}
