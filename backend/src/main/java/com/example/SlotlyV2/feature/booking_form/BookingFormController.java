package com.example.SlotlyV2.feature.booking_form;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @PostMapping("/{eventId}/booking-form")
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse<BookingFormResponse> createBookingForm(
            @PathVariable Long eventId,
            @Valid @RequestBody BookingFormRequest request) {
        return DataResponse.of(bookingFormService.createForm(eventId, request));
    }

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

    @GetMapping("/share/{shareableId}/booking-form")
    public DataResponse<BookingFormResponse> getPublicBookingForm(@PathVariable String shareableId) {
        return DataResponse.of(bookingFormService.getPublicForm(shareableId));
    }
}
