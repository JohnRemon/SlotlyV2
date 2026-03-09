package com.example.SlotlyV2.feature.event.dto;

import java.time.OffsetDateTime;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormFieldResponse;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormResponse;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.user.dto.UserResponse;

public class PublicEventResponse {
    private final Long id;
    private final String eventName;
    private final String description;
    private final OffsetDateTime eventStart;
    private final OffsetDateTime eventEnd;
    private final UserResponse host;
    private final Integer slotDurationMinutes;
    private final BookingFormResponse bookingForm;

    public PublicEventResponse(Event event, TimeZoneConverter timeZoneConverter) {
        this.id = event.getId();
        this.eventName = event.getEventName();
        this.description = event.getDescription();
        this.eventStart = timeZoneConverter.toUtc(event.getEventStart());
        this.eventEnd = timeZoneConverter.toUtc(event.getEventEnd());
        this.host = UserResponse.builder()
                .firstName(event.getHost().getFirstName())
                .lastName(event.getHost().getLastName())
                .build();
        this.slotDurationMinutes = event.getAvailabilityRules().getSlotDurationMinutes();
        this.bookingForm = event.getBookingForm() != null
                ? BookingFormResponse.builder()
                        .fields(event.getBookingForm().getFields().stream()
                                .map(BookingFormFieldResponse::new)
                                .toList())
                        .build()
                : null;

    }
}
