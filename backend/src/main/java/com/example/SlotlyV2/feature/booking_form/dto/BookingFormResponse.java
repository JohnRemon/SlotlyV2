package com.example.SlotlyV2.feature.booking_form.dto;

import java.util.List;

import com.example.SlotlyV2.feature.booking_form.BookingForm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFormResponse {
    private List<BookingFormFieldResponse> fields;

    public BookingFormResponse(BookingForm bookingForm) {
        this.fields = bookingForm.getFields().stream()
                .map(BookingFormFieldResponse::new)
                .toList();
    }
}
