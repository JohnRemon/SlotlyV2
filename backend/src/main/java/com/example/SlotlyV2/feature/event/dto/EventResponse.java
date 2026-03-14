package com.example.SlotlyV2.feature.event.dto;

import com.example.SlotlyV2.feature.availability.dto.AvailabilityRulesDTO;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormResponse;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.schedule.dto.ScheduleResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class EventResponse {
    @JsonProperty(index = 0)
    private final Long id;

    @JsonProperty(index = 5)
    private final String eventName;

    @JsonProperty(index = 10)
    private final String description;

    @JsonProperty(index = 25)
    private final AvailabilityRulesDTO availabilityRules;

    @JsonProperty(index = 35)
    private final String shareableId;

    @JsonProperty(index = 40)
    private final BookingFormResponse bookingForm;

    @JsonProperty(index = 45)
    private final ScheduleResponse schedule;

    public EventResponse(Event event) {
        this.id = event.getId();
        this.eventName = event.getEventName();
        this.description = event.getDescription();
        this.availabilityRules = AvailabilityRulesDTO.builder()
                .slotDurationMinutes(event.getAvailabilityRules().getSlotDurationMinutes())
                .maxSlotsPerUser(event.getAvailabilityRules().getMaxSlotsPerUser())
                .bufferMinutes(event.getAvailabilityRules().getBufferMinutes())
                .minimumNoticeHours(event.getAvailabilityRules().getMinimumNoticeHours())
                .maximumAdvanceDays(event.getAvailabilityRules().getMaximumAdvanceDays())
                .maxCapacity(event.getAvailabilityRules().getMaxCapacity())
                .allowCancellations(event.getAvailabilityRules().getAllowsCancellations())
                .isPublic(event.getAvailabilityRules().getIsPublic())
                .build();
        this.shareableId = event.getShareableId();
        this.bookingForm = new BookingFormResponse(event.getBookingForm());
        this.schedule = new ScheduleResponse(event.getSchedule());
    }
}
