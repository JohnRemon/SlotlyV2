package com.example.SlotlyV2.feature.booking_form.dto;

import com.example.SlotlyV2.feature.booking_form.FieldAnswer;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class BookingFormAnswerResponse {
    @JsonProperty(index = 0)
    private String fieldLabel;

    @JsonProperty(index = 5)
    private String fieldResponse;

    public BookingFormAnswerResponse(FieldAnswer answer) {
        this.fieldLabel = answer.getFormField().getLabel();
        this.fieldResponse = answer.getAnswer();
    }
}
