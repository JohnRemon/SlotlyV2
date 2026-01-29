package com.example.SlotlyV2.feature.event;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.availability.AvailabilityRules;
import com.example.SlotlyV2.feature.availability.dto.AvailabilityRulesDTO;
import com.example.SlotlyV2.feature.event.dto.EventRequest;
import com.example.SlotlyV2.feature.event.dto.RecurringEventRequest;
import com.example.SlotlyV2.feature.recurrence.RecurrenceRules;
import com.example.SlotlyV2.feature.recurrence.dto.RecurrenceRulesDTO;
import com.example.SlotlyV2.feature.user.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventFactory {
    private final UserService userService;
    private final TimeZoneConverter timeZoneConverter;

    public Event createFrom(EventRequest request) {
        AvailabilityRules availabilityRules = buildAvailabilityRules(request.getAvailabilityRulesDTO());
        OffsetDateTime utcStart = timeZoneConverter.toUtc(request.getEventStart(), request.getTimeZone());
        OffsetDateTime utcEnd = timeZoneConverter.toUtc(request.getEventEnd(), request.getTimeZone());

        return Event.builder()
                .eventName(request.getEventName())
                .description(request.getDescription())
                .host(userService.getCurrentUser())
                .eventStart(utcStart)
                .eventEnd(utcEnd)
                .timeZone(request.getTimeZone())
                .availabilityRules(availabilityRules)
                .build();

    }

    public Event createRecurringFrom(RecurringEventRequest request) {
        Event event = createFrom(request);

        RecurrenceRules recurrenceRules = buildRecurrenceRules(request.getRecurringRulesDTO(), request.getTimeZone());

        event.setRecurring(true);
        event.setRecurringRules(recurrenceRules);

        return event;
    }

    public AvailabilityRules buildAvailabilityRules(AvailabilityRulesDTO dto) {
        return AvailabilityRules.builder()
                .slotDurationMinutes(dto.getSlotDurationMinutes())
                .maxSlotsPerUser(dto.getMaxSlotsPerUser())
                .allowsCancellations(dto.isAllowCancellations())
                .isPublic(dto.isPublic())
                .maxCapacity(dto.getMaxCapacity())
                .bufferMinutes(dto.getBufferMinutes())
                .minimumNoticeHours(dto.getMinimumNoticeHours())
                .maximumAdvanceDays(dto.getMaximumAdvanceDays())
                .build();
    }

    public RecurrenceRules buildRecurrenceRules(RecurrenceRulesDTO dto, String timeZone) {
        return RecurrenceRules.builder()
                .recurrenceFrequency(dto.getRecurrenceFrequency())
                .recurrenceEndType(dto.getRecurrenceEndType())
                .recurrenceDayOfWeek(dto.getRecurrenceDayOfWeek())
                .recurrenceOccurrences(dto.getRecurrenceOccurrences())
                .recurrenceEndDate(dto.getRecurrenceEndDate() != null
                        ? timeZoneConverter.toUtc(dto.getRecurrenceEndDate(), timeZone)
                        : null)
                .build();
    }
}
