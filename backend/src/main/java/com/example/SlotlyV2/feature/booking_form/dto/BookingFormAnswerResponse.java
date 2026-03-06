package com.example.SlotlyV2.feature.booking_form.dto;

import com.example.SlotlyV2.feature.booking_form.FieldAnswer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFormAnswerResponse {
    private String fieldLabel;
    private String fieldAnswer;

    public BookingFormAnswerResponse(FieldAnswer answer) {
        this.fieldLabel = answer.getFormField().getLabel();
        this.fieldAnswer = answer.getAnswer();
    }
}
