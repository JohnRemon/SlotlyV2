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

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.feature.booking_form.dto.FormQuestionsView;
import com.example.SlotlyV2.feature.booking_form.dto.FormRequest;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class BookingFormController {
    private final BookingFormService bookingFormService;
    private final EventService eventService;

    @PostMapping("/{eventId}/booking-form")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FormQuestionsView> createBookingForm(@PathVariable Long eventId,
            @Valid @RequestBody FormRequest request) {
        BookingForm bookingForm = bookingFormService.createForm(eventId, request);
        return new ApiResponse<>("Booking form created successfully", new FormQuestionsView(bookingForm));
    }

    @PutMapping("/{eventId}/booking-form")
    public ApiResponse<FormQuestionsView> updateBookingForm(
            @PathVariable Long eventId,
            @Valid @RequestBody FormRequest request) {

        BookingForm bookingForm = bookingFormService.updateForm(eventId, request);
        return new ApiResponse<>("Booking form updated successfully", new FormQuestionsView(bookingForm));
    }

    @GetMapping("/{eventId}/booking-form")
    public ApiResponse<FormQuestionsView> getBookingForm(@PathVariable Long eventId) {
        BookingForm bookingForm = bookingFormService.getForm(eventId);
        return new ApiResponse<>("Booking form retrieved successfully", new FormQuestionsView(bookingForm));
    }

    @GetMapping("share/{shareableId}/booking-form")
    public ApiResponse<FormQuestionsView> getBookingForm(@PathVariable String shareableId) {
        Event event = eventService.getEventByShareableId(shareableId);
        BookingForm bookingForm = bookingFormService.getForm(event.getId());
        return new ApiResponse<>("Booking form retrieved successfully", new FormQuestionsView(bookingForm));
    }
}
