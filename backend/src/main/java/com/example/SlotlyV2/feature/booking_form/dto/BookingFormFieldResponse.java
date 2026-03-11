package com.example.SlotlyV2.feature.booking_form.dto;

import java.util.UUID;

import com.example.SlotlyV2.feature.booking_form.FormQuestion;
import com.example.SlotlyV2.feature.booking_form.enums.FieldType;

import lombok.Value;

@Value
public class BookingFormFieldResponse {
    private UUID id;
    private String label;
    private FieldType fieldType;
    private boolean required;
    private Integer displayOrder;

    public BookingFormFieldResponse(FormQuestion field) {
        this.id = field.getId();
        this.label = field.getLabel();
        this.fieldType = field.getFieldType();
        this.required = field.isRequired();
        this.displayOrder = field.getDisplayOrder();
    }
}
