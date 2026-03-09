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
    public DataResponse<BookingFormResponse> createBookingForm(@PathVariable Long eventId,
            @Valid @RequestBody BookingFormRequest request) {
        BookingForm bookingForm = bookingFormService.createForm(eventId, request);
        return DataResponse.of(new BookingFormResponse(bookingForm));
    }

    @PutMapping("/{eventId}/booking-form")
    public DataResponse<BookingFormResponse> updateBookingForm(
            @PathVariable Long eventId,
            @Valid @RequestBody BookingFormRequest request) {

        BookingForm bookingForm = bookingFormService.updateForm(eventId, request);
        return DataResponse.of(new BookingFormResponse(bookingForm));
    }

    @GetMapping("/{eventId}/booking-form")
    public DataResponse<BookingFormResponse> getBookingForm(@PathVariable Long eventId) {
        BookingForm bookingForm = bookingFormService.getForm(eventId);
        return DataResponse.of(new BookingFormResponse(bookingForm));
    }

    @GetMapping("share/{shareableId}/booking-form")
    public DataResponse<BookingFormResponse> getBookingForm(@PathVariable String shareableId) {
        Event event = eventService.getEventByShareableId(shareableId);
        BookingForm bookingForm = bookingFormService.getForm(event.getId());
        return DataResponse.of(new BookingFormResponse(bookingForm));
    }
}
