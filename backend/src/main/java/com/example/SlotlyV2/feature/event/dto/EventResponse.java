package com.example.SlotlyV2.feature.event.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.availability.dto.AvailabilityRulesDTO;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormResponse;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.recurrence.dto.RecurrenceRulesDTO;

import lombok.Value;

@Value
public class EventResponse {
    private final Long id;
    private final String eventName;
    private final String description;
    private final OffsetDateTime eventStart;
    private final OffsetDateTime eventEnd;
    private final AvailabilityRulesDTO availabilityRules;
    private final RecurrenceRulesDTO recurringRules;
    private final String shareableId;
    private final BookingFormResponse bookingForm;
    private final UUID scheduleId;
    private final Boolean scheduleIsDefault;

    public EventResponse(Event event, TimeZoneConverter timeZoneConverter) {
        this.id = event.getId();
        this.eventName = event.getEventName();
        this.description = event.getDescription();
        this.eventStart = timeZoneConverter.toUtc(event.getEventStart());
        this.eventEnd = timeZoneConverter.toUtc(event.getEventEnd());
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

        this.recurringRules = event.getRecurrenceRules() != null
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
        this.bookingForm = event.getBookingForm() != null
                ? new BookingFormResponse(event.getBookingForm())
                : null;
        this.scheduleId = event.getSchedule().getId();
        this.scheduleIsDefault = event.getSchedule().getIsDefault();
    }
}
