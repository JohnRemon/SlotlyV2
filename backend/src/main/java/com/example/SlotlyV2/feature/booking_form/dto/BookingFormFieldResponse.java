package com.example.SlotlyV2.feature.booking_form.dto;

import com.example.SlotlyV2.feature.booking_form.FormQuestion;
import com.example.SlotlyV2.feature.booking_form.enums.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFormFieldResponse {
    private String label;
    private FieldType fieldType;
    private boolean required;
    private Integer displayOrder;

    public BookingFormFieldResponse(FormQuestion field) {
        this.label = field.getLabel();
        this.fieldType = field.getFieldType();
        this.required = field.isRequired();
        this.displayOrder = field.getDisplayOrder();
    }
}
