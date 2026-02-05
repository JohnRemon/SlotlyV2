package com.example.SlotlyV2.feature.schedule;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.exception.schedule.InvalidScheduleException;
import com.example.SlotlyV2.common.exception.schedule.ScheduleNotFoundException;
import com.example.SlotlyV2.feature.calendar.GoogleCalendarService;
import com.example.SlotlyV2.feature.schedule.dto.BlockedPeriodRequest;
import com.example.SlotlyV2.feature.schedule.dto.BlockedPeriodResponse;
import com.example.SlotlyV2.feature.schedule.dto.DailyScheduleRequest;
import com.example.SlotlyV2.feature.schedule.dto.DailyScheduleResponse;
import com.example.SlotlyV2.feature.schedule.dto.ScheduleRequest;
import com.example.SlotlyV2.feature.user.User;
import com.google.api.services.calendar.model.Event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final DailyScheduleRepository dailyScheduleRepository;
    private final BlockedPeriodRepository blockedPeriodRepository;
    private final GoogleCalendarService googleCalendarService;

    @Transactional(rollbackOn = Exception.class)
    public void updateSchedule(User user, ScheduleRequest request) {

        for (DailyScheduleRequest day : request.getDays()) {
            if (day.isAvailable() && (day.getStartTime() == null || day.getEndTime() == null)) {
                throw new InvalidScheduleException("Start and end time required for available days");
            }

            DailySchedule existing = dailyScheduleRepository.findByUserIdAndDayOfWeek(user.getId(), day.getDayOfWeek())
                    .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found"));

            existing.setStartTime(day.getStartTime());
            existing.setEndTime(day.getEndTime());
            existing.setAvailable(day.isAvailable());

            dailyScheduleRepository.save(existing);
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void createBlockedPeriod(User user, BlockedPeriodRequest request) {
        if (!validateTimeBlock(user, request.getStartTime(), request.getEndTime())) {
            throw new InvalidScheduleException("Time block overlaps with existing block");
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new InvalidScheduleException("End time must be after start time");
        }

        BlockedPeriod blockedPeriod = BlockedPeriod.builder()
                .user(user)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .reason(request.getReason())
                .isRecurring(request.isRecurring())
                .build();

        blockedPeriodRepository.save(blockedPeriod);
    }

    public List<DailyScheduleResponse> getSchedule(User user) {
        return dailyScheduleRepository.findByUserIdOrderByDayOfWeek(user.getId())
                .stream()
                .map(DailyScheduleResponse::new)
                .toList();
    }

    public List<BlockedPeriodResponse> getBlockedPeriods(User user) {
        return blockedPeriodRepository.findByUserId(user.getId())
                .stream()
                .map(BlockedPeriodResponse::new)
                .toList();
    }

    public boolean validateTimeBlock(User user, OffsetDateTime start, OffsetDateTime end) {
        List<BlockedPeriod> overlapping = blockedPeriodRepository
                .findByUserIdAndEndTimeAfterAndStartTimeBefore(
                        user.getId(), start, end);
        return overlapping.isEmpty();
    }

    public boolean isValidSlot(User user, OffsetDateTime start, Integer slotDuration) {
        ZoneId userZone = ZoneId.of(user.getTimeZone());
        Integer dayOfWeek = start.atZoneSameInstant(userZone).getDayOfWeek().getValue();

        DailySchedule day = dailyScheduleRepository.findByUserIdAndDayOfWeek(user.getId(), dayOfWeek)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found"));

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

        List<Event> events = googleCalendarService.getUpcomingEvents(user, start, end);
        if (!events.isEmpty()) {
            return false;
        }

        return true;
    }

    public void createDefaultSchedule(User user) {
        createDay(user, 1, 9, 17, true);
        createDay(user, 2, 9, 17, true);
        createDay(user, 3, 9, 17, true);
        createDay(user, 4, 9, 17, true);
        createDay(user, 5, 9, 17, true);
        createDay(user, 6, 0, 0, false);
        createDay(user, 7, 0, 0, false);
    }

    private void createDay(User user, int dayOfWeek, int startHour, int endHour, boolean available) {
        DailySchedule schedule = DailySchedule.builder()
                .user(user)
                .dayOfWeek(dayOfWeek)
                .startTime(LocalTime.of(startHour, 0))
                .endTime(LocalTime.of(endHour, 0))
                .isAvailable(available)
                .build();
        dailyScheduleRepository.save(schedule);
    }
}
