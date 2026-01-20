package com.example.SlotlyV2.feature.event.dto;

import java.time.LocalDateTime;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.availability.AvailabilityRulesDTO;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.RecurrenceRulesDTO;
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
    private LocalDateTime eventStart;
    private LocalDateTime eventEnd;
    private String timeZone;
    private LocalDateTime createdAt;
    private AvailabilityRulesDTO availabilityRulesDTO;
    private boolean isRecurring;
    private RecurrenceRulesDTO recurringRulesDTO;
    private String shareableId;

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
                .allowCancellations(event.getAvailabilityRules().isAllowsCancellations())
                .isPublic(event.getAvailabilityRules().isPublic())
                .build();

        this.isRecurring = event.isRecurring();
        this.recurringRulesDTO = event.getRecurringRules() != null
                ? RecurrenceRulesDTO.builder()
                        .recurrenceDayOfWeek(event.getRecurringRules().getRecurrenceDayOfWeek())
                        .recurrenceEndDate(event.getRecurringRules().getRecurrenceEndDate() != null
                                ? timeZoneConverter.toUserTimezone(event.getRecurringRules().getRecurrenceEndDate(), userTimezone)
                                : null)
                        .recurrenceFrequency(event.getRecurringRules().getRecurrenceFrequency())
                        .recurrenceOccurrences(event.getRecurringRules().getRecurrenceOccurrences())
                        .recurrenceEndType(event.getRecurringRules().getRecurrenceEndType())
                        .build()
                : null;

        this.shareableId = event.getShareableId();
    }
}
