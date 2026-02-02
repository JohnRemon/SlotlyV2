package com.example.SlotlyV2.feature.custom_form;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.common.exception.custom_form.InvalidFormResponseException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingFormValidator {

    public void validateRequiredFields(List<FormField> fields, String response) {
        for (FormField field : fields) {
            if (field.isRequired() && (response == null || response.trim().isEmpty())) {
                throw new InvalidFormResponseException("Please fill all required fields");
            }
        }
    }
}
