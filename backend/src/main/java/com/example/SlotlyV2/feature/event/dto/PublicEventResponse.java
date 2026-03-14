package com.example.SlotlyV2.feature.event.dto;

import com.example.SlotlyV2.feature.booking_form.dto.BookingFormResponse;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.schedule.dto.ScheduleResponse;
import com.example.SlotlyV2.feature.user.dto.PublicUserResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class PublicEventResponse {
    @JsonProperty(index = 0)
    private final String eventName;

    @JsonProperty(index = 5)
    private final String description;

    @JsonProperty(index = 20)
    private final PublicUserResponse host;

    @JsonProperty(index = 25)
    private final Integer slotDurationMinutes;

    @JsonProperty(index = 30)
    private final BookingFormResponse bookingForm;

    @JsonProperty(index = 35)
    private final ScheduleResponse schedule;

    public PublicEventResponse(Event event) {
        this.eventName = event.getEventName();
        this.description = event.getDescription();
        this.host = new PublicUserResponse(event.getHost());
        this.slotDurationMinutes = event.getAvailabilityRules().getSlotDurationMinutes();
        this.bookingForm = new BookingFormResponse(event.getBookingForm());
        this.schedule = new ScheduleResponse(event.getSchedule());
    }
}
