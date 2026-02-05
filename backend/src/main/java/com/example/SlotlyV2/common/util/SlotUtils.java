package com.example.SlotlyV2.common.util;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.common.exception.event.InvalidEventException;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.enums.RecurrenceEndType;
import com.example.SlotlyV2.feature.recurrence.RecurrenceRules;
import com.example.SlotlyV2.feature.schedule.ScheduleService;
import com.example.SlotlyV2.feature.slot.Slot;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SlotUtils {
    private final static Integer MAX_YEARS = 1;
    private final ScheduleService scheduleService;

    public List<Slot> buildSlotsByTime(Event event, OffsetDateTime start, OffsetDateTime end) {
        List<Slot> slots = new ArrayList<>();
        OffsetDateTime currentStart = start;

        Integer slotDuration = event.getAvailabilityRules().getSlotDurationMinutes();
        Integer buffer = event.getAvailabilityRules().getBufferMinutes();

        while (currentStart.plusMinutes(slotDuration).isBefore(end)
                || currentStart.plusMinutes(slotDuration).equals(end)) {

            OffsetDateTime slotEnd = currentStart.plusMinutes(slotDuration);

            if (scheduleService.isValidSlot(event.getHost(), currentStart, slotDuration)) {
                Slot slot = Slot.builder()
                        .event(event)
                        .startTime(currentStart)
                        .endTime(slotEnd)
                        .build();
                slots.add(slot);
            }
            currentStart = slotEnd.plusMinutes(buffer);
        }
        return slots;
    }

    public List<Slot> buildRecurringSlots(Event event) {
        List<Slot> slots = new ArrayList<>();

        OffsetDateTime currentStart = event.getEventStart();

        Duration eventDuration = Duration.between(event.getEventStart(), event.getEventEnd());

        RecurrenceRules rules = event.getRecurrenceRules();

        OffsetDateTime end = rules.getRecurrenceEndDate();

        RecurrenceEndType recurrenceEndType = rules.getRecurrenceEndType();

        if (recurrenceEndType == RecurrenceEndType.DATE && end == null) {
            throw new InvalidEventException("End date of recurrence is required");
        }

        if (rules.getRecurrenceEndType() == RecurrenceEndType.NEVER) {
            end = event.getEventStart().plusYears(MAX_YEARS);
        }

        while (!currentStart.isAfter(end)) {
            OffsetDateTime currentEnd = currentStart.plus(eventDuration);
            slots.addAll(buildSlotsByTime(event, currentStart, currentEnd));
            currentStart = getNextRecurrence(currentStart, rules);
        }

        return slots;
    }

    public List<Slot> buildRecurringSlotsByOccurrences(Event event) {
        List<Slot> slots = new ArrayList<>();

        OffsetDateTime currentStart = event.getEventStart();

        Duration eventDuration = Duration.between(event.getEventStart(), event.getEventEnd());

        RecurrenceRules rules = event.getRecurrenceRules();

        if (rules.getRecurrenceOccurrences() == null) {
            throw new InvalidEventException("Occurrences count is required");
        }

        Integer count = 0;

        while (count < rules.getRecurrenceOccurrences()) {
            OffsetDateTime currentEnd = currentStart.plus(eventDuration);
            slots.addAll(buildSlotsByTime(event, currentStart, currentEnd));
            currentStart = getNextRecurrence(currentStart, rules);
            count++;
        }

        return slots;
    }

    private OffsetDateTime getNextRecurrence(OffsetDateTime current, RecurrenceRules rules) {
        return switch (rules.getRecurrenceFrequency()) {
            case DAILY -> current.plusDays(1);
            case WEEKLY -> current.plusWeeks(1);
            case MONTHLY -> current.plusMonths(1);
            case CUSTOM -> current.plusDays(rules.getInterval());
        };
    }
}
