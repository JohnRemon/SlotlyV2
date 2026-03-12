package com.example.SlotlyV2.feature.booking_form.dto;

import java.util.UUID;

import com.example.SlotlyV2.feature.booking_form.FormQuestion;
import com.example.SlotlyV2.feature.booking_form.enums.FieldType;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class BookingFormFieldResponse {
    @JsonProperty(index = 0)
    private UUID id;

    @JsonProperty(index = 5)
    private String label;

    @JsonProperty(index = 10)
    private FieldType fieldType;

    @JsonProperty(index = 15)
    private boolean required;

    @JsonProperty(index = 20)
    private Integer displayOrder;

    public BookingFormFieldResponse(FormQuestion field) {
        this.id = field.getId();
        this.label = field.getLabel();
        this.fieldType = field.getFieldType();
        this.required = field.isRequired();
        this.displayOrder = field.getDisplayOrder();
    }
}
