package com.example.SlotlyV2.feature.custom_form.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.example.SlotlyV2.feature.custom_form.BookingForm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFormResponse {
    private List<FormFieldDTO> fields;

    public BookingFormResponse(BookingForm bookingForm) {
        this.fields = bookingForm.getFields().stream()
                .map(formField -> FormFieldDTO.builder()
                        .label(formField.getLabel())
                        .fieldType(formField.getFieldType())
                        .required(formField.isRequired())
                        .displayOrder(formField.getDisplayOrder())
                        .build())
                .collect(Collectors.toList());
    }
}
