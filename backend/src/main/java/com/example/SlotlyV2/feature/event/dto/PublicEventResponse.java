package com.example.SlotlyV2.feature.event.dto;

import java.time.OffsetDateTime;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormResponse;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.user.dto.PublicUserResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class PublicEventResponse {
    @JsonProperty(index = 0)
    private final String eventName;

    @JsonProperty(index = 5)
    private final String description;

    @JsonProperty(index = 10)
    private final OffsetDateTime eventStart;

    @JsonProperty(index = 15)
    private final OffsetDateTime eventEnd;

    @JsonProperty(index = 20)
    private final PublicUserResponse host;

    @JsonProperty(index = 25)
    private final Integer slotDurationMinutes;

    @JsonProperty(index = 30)
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
