package com.example.SlotlyV2.feature.schedule;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.exception.schedule.InvalidScheduleException;
import com.example.SlotlyV2.common.exception.schedule.ScheduleNotFoundException;
import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.schedule.dto.BlockedPeriodRequest;
import com.example.SlotlyV2.feature.schedule.dto.BlockedPeriodResponse;
import com.example.SlotlyV2.feature.schedule.dto.DailyScheduleRequest;
import com.example.SlotlyV2.feature.schedule.dto.DailyScheduleResponse;
import com.example.SlotlyV2.feature.schedule.dto.ScheduleRequest;
import com.example.SlotlyV2.feature.user.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final DailyScheduleRepository dailyScheduleRepository;
    private final BlockedPeriodRepository blockedPeriodRepository;
    private final TimeZoneConverter timeZoneConverter;

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

    public boolean isValidSlot(User user, LocalDateTime start, Integer slotDuration) {
        Integer dayOfWeek = start.getDayOfWeek().getValue();
        DailySchedule day = dailyScheduleRepository.findByUserIdAndDayOfWeek(user.getId(), dayOfWeek)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found"));

        LocalDateTime end = start.plusMinutes(slotDuration);

        if (!day.isAvailable()) {
            return false;
        }

        if (end.toLocalTime().isBefore(start.toLocalTime())) {
            return false;
        }
        if (start.toLocalTime().isBefore(day.getStartTime()) || end.toLocalTime().isAfter(day.getEndTime())) {
            return false;
        }
        OffsetDateTime utcStart = timeZoneConverter.toUtc(start, user.getTimeZone());
        OffsetDateTime utcEnd = timeZoneConverter.toUtc(end, user.getTimeZone());

        List<BlockedPeriod> blocks = blockedPeriodRepository.findByUserId(user.getId());
        for (BlockedPeriod block : blocks) {
            if (utcStart.isBefore(block.getEndTime()) && utcEnd.isAfter(block.getStartTime())) {
                return false;
            }
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
