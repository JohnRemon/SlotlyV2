package com.example.SlotlyV2.common.util;

import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.common.exception.event.InvalidEventException;
import com.example.SlotlyV2.common.exception.schedule.ScheduleNotFoundException;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.enums.RecurrenceEndType;
import com.example.SlotlyV2.feature.recurrence.RecurrenceRules;
import com.example.SlotlyV2.feature.schedule.BlockedPeriod;
import com.example.SlotlyV2.feature.schedule.BlockedPeriodRepository;
import com.example.SlotlyV2.feature.schedule.DailySchedule;
import com.example.SlotlyV2.feature.schedule.Schedule;
import com.example.SlotlyV2.feature.slot.Slot;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.calendar.GoogleCalendarService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SlotUtils {
    private final static Integer MAX_YEARS = 1;
    private final BlockedPeriodRepository blockedPeriodRepository;
    private final GoogleCalendarService googleCalendarService;

    public List<Slot> buildSlotsByTime(Schedule schedule, Event event, OffsetDateTime start, OffsetDateTime end) {
        List<Slot> slots = new ArrayList<>();
        OffsetDateTime currentStart = start;

        Integer slotDuration = event.getAvailabilityRules().getSlotDurationMinutes();
        Integer buffer = event.getAvailabilityRules().getBufferMinutes();

        while (currentStart.plusMinutes(slotDuration).isBefore(end)
                || currentStart.plusMinutes(slotDuration).equals(end)) {

            OffsetDateTime slotEnd = currentStart.plusMinutes(slotDuration);

            if (isValidSlot(schedule, event.getHost(), currentStart, slotDuration)) {
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

    public List<Slot> buildRecurringSlots(Event event, Schedule schedule) {
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
            slots.addAll(buildSlotsByTime(schedule, event, currentStart, currentEnd));
            currentStart = getNextRecurrence(currentStart, rules);
        }

        return slots;
    }

    public List<Slot> buildRecurringSlotsByOccurrences(Event event, Schedule schedule) {
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
            slots.addAll(buildSlotsByTime(schedule, event, currentStart, currentEnd));
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

    private boolean isValidSlot(Schedule schedule, User user, OffsetDateTime start, Integer slotDuration) {
        ZoneId userZone = ZoneId.of(user.getTimeZone());
        Integer dayOfWeek = start.atZoneSameInstant(userZone).getDayOfWeek().getValue();

        DailySchedule day = schedule.getDailySchedules().stream()
                .filter(scheduleDay -> scheduleDay.getDayOfWeek().equals(dayOfWeek))
                .findFirst()
                .orElseThrow(() -> new ScheduleNotFoundException("Daily Schedule not found"));

        if (!day.isAvailable()) {
            return false;
        }

        OffsetDateTime end = start.plusMinutes(slotDuration);

        LocalTime startTime = start.atZoneSameInstant(userZone).toLocalTime();
        LocalTime endTime = end.atZoneSameInstant(userZone).toLocalTime();

        if (startTime.isBefore(day.getStartTime()) || endTime.isAfter(day.getEndTime())) {
            return false;
        }

        if (endTime.isBefore(startTime)) {
            return false;
        }

        List<BlockedPeriod> blocks = blockedPeriodRepository
                .findByUserIdAndEndTimeAfterAndStartTimeBefore(user.getId(), start, end);

        if (!blocks.isEmpty()) {
            return false;
        }

        List<com.google.api.services.calendar.model.Event> events = googleCalendarService.getUpcomingEvents(user, start, end);
        return events.isEmpty();
    }
}
