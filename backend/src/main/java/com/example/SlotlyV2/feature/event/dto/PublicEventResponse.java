package com.example.SlotlyV2.feature.event.dto;

import java.time.OffsetDateTime;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormResponse;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.user.dto.PublicUserResponse;

import lombok.Value;

@Value
public class PublicEventResponse {
    private final String eventName;
    private final String description;
    private final OffsetDateTime eventStart;
    private final OffsetDateTime eventEnd;
    private final PublicUserResponse host;
    private final Integer slotDurationMinutes;
    private final BookingFormResponse bookingForm;

    public PublicEventResponse(Event event, TimeZoneConverter timeZoneConverter) {
        this.eventName = event.getEventName();
        this.description = event.getDescription();
        this.eventStart = timeZoneConverter.toUtc(event.getEventStart());
        this.eventEnd = timeZoneConverter.toUtc(event.getEventEnd());
        this.host = new PublicUserResponse(event.getHost());
        this.slotDurationMinutes = event.getAvailabilityRules().getSlotDurationMinutes();
        this.bookingForm = new BookingFormResponse(event.getBookingForm());
    }
}
