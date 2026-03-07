package com.example.SlotlyV2.feature.schedule;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.exception.schedule.BlockedPeriodNotFoundException;
import com.example.SlotlyV2.common.exception.schedule.InvalidScheduleException;
import com.example.SlotlyV2.common.exception.schedule.ScheduleNotFoundException;
import com.example.SlotlyV2.feature.calendar.GoogleCalendarService;
import com.example.SlotlyV2.feature.schedule.dto.BlockedPeriodRequest;
import com.example.SlotlyV2.feature.schedule.dto.DailyScheduleRequest;
import com.example.SlotlyV2.feature.schedule.dto.ScheduleRequest;
import com.example.SlotlyV2.feature.user.User;
import com.google.api.services.calendar.model.Event;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final BlockedPeriodRepository blockedPeriodRepository;
    private final GoogleCalendarService googleCalendarService;

    // TODO: separate the blocked periods into a separate service and controller

    public Schedule getSchedule(UUID id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule Not Found"));
    }

    public List<Schedule> getSchedules(User user) {
        return scheduleRepository.findAllByUser(user);
    }

    @Transactional
    public Schedule createSchedule(User user, ScheduleRequest request) {
        List<DailySchedule> dailySchedules = new ArrayList<>();
        for (DailyScheduleRequest day : request.getDays()) {

            if (day.isAvailable() && (day.getStartTime() == null || day.getEndTime() == null)) {
                throw new InvalidScheduleException("Start and end time required for available days");
            }

            DailySchedule dailySchedule = DailySchedule.builder()
                    .dayOfWeek(day.getDayOfWeek())
                    .startTime(day.getStartTime())
                    .endTime(day.getEndTime())
                    .isAvailable(day.isAvailable())
                    .build();

            dailySchedules.add(dailySchedule);
        }

        Schedule schedule = Schedule.builder()
                .user(user)
                .dailySchedules(dailySchedules)
                .build();

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public Schedule updateSchedule(ScheduleRequest request, UUID id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule Not Found"));

        for (DailySchedule day : schedule.getDailySchedules()) {

            if (day.isAvailable() && (day.getStartTime() == null || day.getEndTime() == null)) {
                throw new InvalidScheduleException("Start and end time required for available days");
            }

            day.setStartTime(day.getStartTime());
            day.setEndTime(day.getEndTime());
            day.setAvailable(day.isAvailable());
        }

        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(UUID id) {
        scheduleRepository.deleteById(id);
    }

    public BlockedPeriod getBookingPeriodById(UUID id) {
        return blockedPeriodRepository.findById(id)
                .orElseThrow(() -> new BlockedPeriodNotFoundException("Blocked Period Not Found"));
    }

    public List<BlockedPeriod> getBlockedPeriods(User user) {
        return blockedPeriodRepository.findByUserId(user.getId());
    }

    @Transactional
    public BlockedPeriod createBlockedPeriod(User user, BlockedPeriodRequest request) {
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

        return blockedPeriodRepository.save(blockedPeriod);
    }

    public boolean validateTimeBlock(User user, OffsetDateTime start, OffsetDateTime end) {
        List<BlockedPeriod> overlapping = blockedPeriodRepository
                .findByUserIdAndEndTimeAfterAndStartTimeBefore(
                        user.getId(), start, end);
        return overlapping.isEmpty();
    }

    public boolean isValidSlot(User user, OffsetDateTime start, Integer slotDuration) {
        Schedule schedule = scheduleRepository.findByUser(user)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule Not Found"));

        ZoneId userZone = ZoneId.of(user.getTimeZone());
        Integer dayOfWeek = start.atZoneSameInstant(userZone).getDayOfWeek().getValue();

        DailySchedule day = schedule.getDailySchedules().stream()
                .filter(scheduleDay -> scheduleDay.getDayOfWeek().equals(dayOfWeek))
                .findFirst()
                .orElseThrow(() -> new ScheduleNotFoundException("Daily Schedule Not Found"));

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
        List<DailySchedule> dailySchedules = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            dailySchedules.add(createDay(user, i, 9, 17, true));
        }

        dailySchedules.add(createDay(user, 6, 0, 0, false));
        dailySchedules.add(createDay(user, 7, 0, 0, false));

        Schedule schedule = Schedule.builder()
                .user(user)
                .dailySchedules(dailySchedules)
                .build();

        scheduleRepository.save(schedule);
    }

    private DailySchedule createDay(User user, int dayOfWeek, int startHour, int endHour, boolean available) {
        DailySchedule dailySchedule = DailySchedule.builder()
                .dayOfWeek(dayOfWeek)
                .startTime(LocalTime.of(startHour, 0))
                .endTime(LocalTime.of(endHour, 0))
                .isAvailable(available)
                .build();
        return dailySchedule;
    }

}
