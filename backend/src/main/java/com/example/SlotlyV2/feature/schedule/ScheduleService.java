package com.example.SlotlyV2.feature.schedule;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.exception.schedule.BlockedPeriodNotFoundException;
import com.example.SlotlyV2.common.exception.schedule.InvalidScheduleException;
import com.example.SlotlyV2.common.exception.schedule.ScheduleNotFoundException;
import com.example.SlotlyV2.feature.event.EventRepository;
import com.example.SlotlyV2.feature.schedule.dto.BlockedPeriodRequest;
import com.example.SlotlyV2.feature.schedule.dto.DailyScheduleRequest;
import com.example.SlotlyV2.feature.schedule.dto.ScheduleRequest;
import com.example.SlotlyV2.feature.schedule.dto.UpdateScheduleRequest;
import com.example.SlotlyV2.feature.slot.SlotService;
import com.example.SlotlyV2.feature.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final BlockedPeriodRepository blockedPeriodRepository;
    private final EventRepository eventRepository;
    private final SlotService slotService;

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
        Schedule schedule = createDefaultSchedule(user, request.getName());

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public Schedule updateSchedule(UpdateScheduleRequest request, UUID id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule Not Found"));

        Map<Integer, DailyScheduleRequest> requestMap = request.getDays().stream()
                .collect(Collectors.toMap(DailyScheduleRequest::getDayOfWeek, d -> d));

        for (DailySchedule day : schedule.getDailySchedules()) {
            DailyScheduleRequest updated = requestMap.get(day.getDayOfWeek());
            if (updated == null)
                continue;

            if (updated.getIsAvailable() && (updated.getStartTime() == null || updated.getEndTime() == null)) {
                throw new InvalidScheduleException("Start and end time required for available days");
            }

            day.setStartTime(updated.getStartTime());
            day.setEndTime(updated.getEndTime());
            day.setAvailable(updated.getIsAvailable());
        }

        List<com.example.SlotlyV2.feature.event.Event> affectedEvents = eventRepository
                .findByScheduleAndDeletedAtIsNull(schedule);
        for (com.example.SlotlyV2.feature.event.Event event : affectedEvents) {
            slotService.regenerateFutureSlots(event);
        }

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public Schedule updateScheduleName(String name, UUID id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule Not Found"));

        schedule.setName(name);
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
                .isDefault(true)
                .build();

        scheduleRepository.save(schedule);
    }

    private Schedule createDefaultSchedule(User user, String name) {
        List<DailySchedule> dailySchedules = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            dailySchedules.add(createDay(user, i, 9, 17, true));
        }

        dailySchedules.add(createDay(user, 6, 0, 0, false));
        dailySchedules.add(createDay(user, 7, 0, 0, false));

        Schedule schedule = Schedule.builder()
                .user(user)
                .name(name)
                .dailySchedules(dailySchedules)
                .isDefault(true)
                .build();

        return scheduleRepository.save(schedule);
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
