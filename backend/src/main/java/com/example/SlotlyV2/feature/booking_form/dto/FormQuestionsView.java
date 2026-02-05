package com.example.SlotlyV2.feature.booking_form.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.example.SlotlyV2.feature.booking_form.BookingForm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormQuestionsView {
    private List<FormQuestionDTO> fields;

    public FormQuestionsView(BookingForm bookingForm) {
        this.fields = bookingForm.getFields().stream()
                .map(formField -> FormQuestionDTO.builder()
                        .id(formField.getId())
                        .label(formField.getLabel())
                        .fieldType(formField.getFieldType())
                        .required(formField.isRequired())
                        .displayOrder(formField.getDisplayOrder())
                        .build())
                .collect(Collectors.toList());
    }
}
