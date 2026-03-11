package com.example.SlotlyV2.feature.booking_form;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.DataResponse;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormRequest;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class BookingFormController {
    private final BookingFormService bookingFormService;

    @PutMapping("/{eventId}/booking-form")
    public DataResponse<BookingFormResponse> updateBookingForm(
            @PathVariable Long eventId,
            @Valid @RequestBody BookingFormRequest request) {
        return DataResponse.of(bookingFormService.updateForm(eventId, request));
    }

    @GetMapping("/{eventId}/booking-form")
    public DataResponse<BookingFormResponse> getBookingForm(@PathVariable Long eventId) {
        return DataResponse.of(bookingFormService.getForm(eventId));
    }

    // TODO: fix mapping in frontend
    @GetMapping("/{shareableId}/booking-form/public")
    public DataResponse<BookingFormResponse> getPublicBookingForm(@PathVariable String shareableId) {
        return DataResponse.of(bookingFormService.getPublicForm(shareableId));
    }
}
