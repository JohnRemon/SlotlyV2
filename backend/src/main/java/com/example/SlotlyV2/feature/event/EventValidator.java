package com.example.SlotlyV2.feature.event;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.common.exception.event.InvalidEventException;
import com.example.SlotlyV2.feature.event.dto.EventRequest;
import com.example.SlotlyV2.feature.event.enums.RecurrenceFrequency;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventValidator {

    public void validateEventDates(OffsetDateTime start, OffsetDateTime end) {
        if (!end.isAfter(start)) {
            throw new InvalidEventException("Event end must be after start");
        }

        OffsetDateTime now = OffsetDateTime.now();

        if (start.isBefore(now)) {
            throw new InvalidEventException("Event must start in the future");
        }
    }

    public void validateRecurringEventRules(EventRequest request) {
        if (request.getRecurrenceRulesDTO().getRecurrenceEndDate() != null
                && !request.getRecurrenceRulesDTO().getRecurrenceEndDate()
                        .isAfter(request.getEventStart())) {
            throw new InvalidEventException("Recurrence end date must be after event start");
        }

        if (request.getRecurrenceRulesDTO().getRecurrenceFrequency() == RecurrenceFrequency.WEEKLY
                && request.getRecurrenceRulesDTO().getRecurrenceDayOfWeek() == null) {
            throw new InvalidEventException("Day of week is required for weekly recurrence");
        }
    }

    public void validateNewCapacity(Integer newCapacity, Integer bookedSlots) {
        if (newCapacity != null && bookedSlots < newCapacity) {
            throw new InvalidEventException(
                    "Cannot reduce capacity below current bookings");
        }
    }
}
