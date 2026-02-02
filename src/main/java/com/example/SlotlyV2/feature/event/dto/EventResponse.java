package com.example.SlotlyV2.feature.event.dto;

import java.time.OffsetDateTime;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.availability.dto.AvailabilityRulesDTO;
import com.example.SlotlyV2.feature.custom_form.dto.BookingFormResponse;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.recurrence.dto.RecurrenceRulesDTO;
import com.example.SlotlyV2.feature.user.dto.UserResponse;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class EventResponse {
    private Long id;
    private String eventName;
    private String description;
    private UserResponse host;
    private OffsetDateTime eventStart;
    private OffsetDateTime eventEnd;
    private String timeZone;
    private OffsetDateTime createdAt;
    private AvailabilityRulesDTO availabilityRulesDTO;
    private boolean isRecurring;
    private RecurrenceRulesDTO recurringRulesDTO;
    private String shareableId;
    private BookingFormResponse bookingForm;

    public EventResponse(Event event, String userTimezone, TimeZoneConverter timeZoneConverter) {
        this.id = event.getId();
        this.eventName = event.getEventName();
        this.description = event.getDescription();
        this.host = new UserResponse(event.getHost());
        this.eventStart = timeZoneConverter.toUserTimezone(event.getEventStart(), userTimezone);
        this.eventEnd = timeZoneConverter.toUserTimezone(event.getEventEnd(), userTimezone);
        this.timeZone = event.getTimeZone();
        this.createdAt = event.getCreatedAt();
        this.availabilityRulesDTO = AvailabilityRulesDTO.builder()
                .slotDurationMinutes(event.getAvailabilityRules().getSlotDurationMinutes())
                .maxSlotsPerUser(event.getAvailabilityRules().getMaxSlotsPerUser())
                .allowCancellations(event.getAvailabilityRules().getAllowsCancellations())
                .isPublic(event.getAvailabilityRules().getIsPublic())
                .build();

        this.isRecurring = event.isRecurring();
        this.recurringRulesDTO = event.getRecurrenceRules() != null
                ? RecurrenceRulesDTO.builder()
                        .recurrenceDayOfWeek(event.getRecurrenceRules().getRecurrenceDayOfWeek())
                        .recurrenceEndDate(event.getRecurrenceRules().getRecurrenceEndDate() != null
                                ? timeZoneConverter.toUserTimezone(event.getRecurrenceRules().getRecurrenceEndDate(),
                                        userTimezone)
                                : null)
                        .recurrenceFrequency(event.getRecurrenceRules().getRecurrenceFrequency())
                        .recurrenceOccurrences(event.getRecurrenceRules().getRecurrenceOccurrences())
                        .recurrenceEndType(event.getRecurrenceRules().getRecurrenceEndType())
                        .build()
                : null;

        this.shareableId = event.getShareableId();
        this.bookingForm = event.getBookingForm() != null ? new BookingFormResponse(event.getBookingForm()) : null;
    }
}
