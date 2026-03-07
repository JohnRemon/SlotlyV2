package com.example.SlotlyV2.feature.event.dto;

import java.time.OffsetDateTime;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.availability.dto.AvailabilityRulesDTO;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormResponse;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.recurrence.dto.RecurrenceRulesDTO;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class EventResponse {
    private Long id;
    private String eventName;
    private String description;
    private OffsetDateTime eventStart;
    private OffsetDateTime eventEnd;
    private AvailabilityRulesDTO availabilityRulesDTO;
    private RecurrenceRulesDTO recurringRulesDTO;
    private String shareableId;
    private BookingFormResponse bookingForm;

    public EventResponse(Event event, TimeZoneConverter timeZoneConverter) {
        this.id = event.getId();
        this.eventName = event.getEventName();
        this.description = event.getDescription();
        this.eventStart = timeZoneConverter.toUtc(event.getEventStart());
        this.eventEnd = timeZoneConverter.toUtc(event.getEventEnd());
        this.availabilityRulesDTO = AvailabilityRulesDTO.builder()
                .slotDurationMinutes(event.getAvailabilityRules().getSlotDurationMinutes())
                .maxSlotsPerUser(event.getAvailabilityRules().getMaxSlotsPerUser())
                .allowCancellations(event.getAvailabilityRules().getAllowsCancellations())
                .isPublic(event.getAvailabilityRules().getIsPublic())
                .build();

        this.recurringRulesDTO = event.getRecurrenceRules() != null
                ? RecurrenceRulesDTO.builder()
                        .recurrenceDayOfWeek(event.getRecurrenceRules().getRecurrenceDayOfWeek())
                        .recurrenceEndDate(event.getRecurrenceRules().getRecurrenceEndDate() != null
                                ? timeZoneConverter.toUtc(event.getRecurrenceRules().getRecurrenceEndDate())
                                : null)
                        .recurrenceFrequency(event.getRecurrenceRules().getRecurrenceFrequency())
                        .recurrenceOccurrences(event.getRecurrenceRules().getRecurrenceOccurrences())
                        .recurrenceEndType(event.getRecurrenceRules().getRecurrenceEndType())
                        .build()
                : null;

        this.shareableId = event.getShareableId();
        this.bookingForm = new BookingFormResponse(event.getBookingForm());
    }
}
