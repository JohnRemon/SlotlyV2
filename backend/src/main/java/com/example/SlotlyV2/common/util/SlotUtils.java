package com.example.SlotlyV2.common.util;

import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.common.exception.schedule.ScheduleNotFoundException;
import com.example.SlotlyV2.feature.calendar.GoogleCalendarService;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.schedule.BlockedPeriod;
import com.example.SlotlyV2.feature.schedule.BlockedPeriodRepository;
import com.example.SlotlyV2.feature.schedule.DailySchedule;
import com.example.SlotlyV2.feature.schedule.Schedule;
import com.example.SlotlyV2.feature.slot.Slot;
import com.example.SlotlyV2.feature.user.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SlotUtils {
    private final BlockedPeriodRepository blockedPeriodRepository;
    private final GoogleCalendarService googleCalendarService;

    public List<Slot> buildSlotsByTime(Schedule schedule, Event event, OffsetDateTime start) {
        List<Slot> slots = new ArrayList<>();
        OffsetDateTime currentStart = start;

        Integer slotDuration = event.getAvailabilityRules().getSlotDurationMinutes();
        Integer buffer = event.getAvailabilityRules().getBufferMinutes();
        User host = event.getHost();
        OffsetDateTime end = OffsetDateTime.now(ZoneOffset.UTC)
                .plusDays(event.getAvailabilityRules().getMaximumAdvanceDays());

        List<BlockedPeriod> blockedPeriods = blockedPeriodRepository
                .findByUserIdAndEndTimeAfterAndStartTimeBefore(host.getId(), start, end);

        List<com.google.api.services.calendar.model.Event> calendarEvents = googleCalendarService
                .getUpcomingEvents(host, start, end);

        while (currentStart.plusMinutes(slotDuration).isBefore(end)
                || currentStart.plusMinutes(slotDuration).equals(end)) {

            OffsetDateTime slotEnd = currentStart.plusMinutes(slotDuration);

            if (isValidSlot(schedule, event.getHost(), currentStart, slotDuration, blockedPeriods, calendarEvents)) {
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

    private boolean isValidSlot(Schedule schedule, User user, OffsetDateTime start, Integer slotDuration,
            List<BlockedPeriod> blockedPeriods, List<com.google.api.services.calendar.model.Event> calendarEvents) {

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

        boolean blockedByPeriod = blockedPeriods.stream()
                .anyMatch(b -> b.getStartTime().isBefore(end) && b.getEndTime().isAfter(start));

        if (blockedByPeriod) {
            return false;
        }

        boolean blockedByCalendar = calendarEvents.stream()
                .anyMatch(e -> {
                    OffsetDateTime eventStart = OffsetDateTime
                            .ofInstant(Instant.ofEpochMilli(e.getStart().getDateTime().getValue()), ZoneOffset.UTC);
                    OffsetDateTime eventEnd = OffsetDateTime
                            .ofInstant(Instant.ofEpochMilli(e.getEnd().getDateTime().getValue()), ZoneOffset.UTC);
                    return eventStart.isBefore(end) && eventEnd.isAfter(start);
                });

        return !blockedByCalendar;
    }
}
