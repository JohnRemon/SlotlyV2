package com.example.SlotlyV2.feature.schedule;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.exception.auth.ForbiddenException;
import com.example.SlotlyV2.common.exception.schedule.InvalidScheduleException;
import com.example.SlotlyV2.common.exception.schedule.ScheduleNotFoundException;
import com.example.SlotlyV2.feature.event.EventRepository;
import com.example.SlotlyV2.feature.schedule.dto.DailyScheduleRequest;
import com.example.SlotlyV2.feature.schedule.dto.ScheduleRequest;
import com.example.SlotlyV2.feature.schedule.dto.ScheduleResponse;
import com.example.SlotlyV2.feature.schedule.dto.UpdateScheduleRequest;
import com.example.SlotlyV2.feature.slot.SlotService;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final EventRepository eventRepository;
    private final SlotService slotService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public ScheduleResponse getSchedule(UUID id) {
        User currentUser = userService.getCurrentUser();
        return toResponse(findAndAuthorizeSchedule(currentUser, id));
    }

    @Transactional(readOnly = true)
    public Page<ScheduleResponse> getSchedules(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return scheduleRepository.findAllByUser(currentUser, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public ScheduleResponse createSchedule(ScheduleRequest request) {
        User currentUser = userService.getCurrentUser();
        Schedule schedule = buildScheduleWithDefaults(currentUser, request.getName(), false);
        log.info("Schedule created scheduleId={} userId={}", schedule.getId(), currentUser.getId());
        return toResponse(schedule);
    }

    @Transactional
    public ScheduleResponse updateSchedule(UpdateScheduleRequest request, UUID id) {
        User currentUser = userService.getCurrentUser();
        Schedule schedule = findAndAuthorizeSchedule(currentUser, id);

        Map<Integer, DailyScheduleRequest> requestMap = request.getDays().stream()
                .collect(Collectors.toMap(DailyScheduleRequest::getDayOfWeek, d -> d));

        for (DailySchedule day : schedule.getDailySchedules()) {
            DailyScheduleRequest updated = requestMap.get(day.getDayOfWeek());
            if (updated == null)
                continue;

            if (updated.getIsAvailable() && (updated.getStartTime() == null || updated.getEndTime() == null)) {
                throw new InvalidScheduleException("Start and end time are required for available days");
            }

            day.setStartTime(updated.getStartTime());
            day.setEndTime(updated.getEndTime());
            day.setAvailable(updated.getIsAvailable());
        }

        scheduleRepository.save(schedule);

        eventRepository.findByScheduleAndDeletedAtIsNull(schedule)
                .forEach(slotService::regenerateFutureSlots);

        log.info("Schedule updated scheduleId={} userId={}", id, currentUser.getId());
        return toResponse(schedule);
    }

    @Transactional
    public ScheduleResponse updateScheduleName(String name, UUID id) {
        User currentUser = userService.getCurrentUser();
        Schedule schedule = findAndAuthorizeSchedule(currentUser, id);
        schedule.setName(name);
        log.info("Schedule renamed scheduleId={} userId={}", id, currentUser.getId());
        return toResponse(scheduleRepository.save(schedule));
    }

    @Transactional
    public ScheduleResponse updateDefaultSchedule(UUID id) {
        User currentUser = userService.getCurrentUser();

        Schedule currentDefault = scheduleRepository.findByUserAndIsDefaultTrue(currentUser)
                .orElseThrow(() -> new ScheduleNotFoundException("Default schedule not found"));
        currentDefault.setIsDefault(false);
        scheduleRepository.save(currentDefault);

        Schedule schedule = findAndAuthorizeSchedule(currentUser, id);
        schedule.setIsDefault(true);
        log.info("Default schedule updated scheduleId={} userId={}", id, currentUser.getId());
        return toResponse(scheduleRepository.save(schedule));
    }

    @Transactional
    public void deleteSchedule(UUID id) {
        User currentUser = userService.getCurrentUser();
        Schedule schedule = findAndAuthorizeSchedule(currentUser, id);

        if (scheduleRepository.countByUser(currentUser) == 1) {
            throw new InvalidScheduleException("Cannot delete the only schedule");
        }

        if (schedule.getIsDefault()) {
            throw new InvalidScheduleException("Cannot delete the default schedule — set a new default first");
        }

        scheduleRepository.delete(schedule);
        log.info("Schedule deleted scheduleId={} userId={}", id, currentUser.getId());
    }

    // Called during user registration — no auth context available
    public void createDefaultScheduleForUser(User user) {
        buildScheduleWithDefaults(user, "Working hours", true);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Schedule findAndAuthorizeSchedule(User user, UUID id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + id));

        if (!schedule.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized schedule access attempt scheduleId={} userId={}", id, user.getId());
            throw new ForbiddenException("You are not authorized to access this resource");
        }

        return schedule;
    }

    private Schedule buildScheduleWithDefaults(User user, String name, boolean isDefault) {
        List<DailySchedule> dailySchedules = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            dailySchedules.add(buildDay(i, LocalTime.of(9, 0), LocalTime.of(17, 0), true));
        }
        dailySchedules.add(buildDay(6, LocalTime.of(0, 0), LocalTime.of(0, 0), false));
        dailySchedules.add(buildDay(7, LocalTime.of(0, 0), LocalTime.of(0, 0), false));

        Schedule schedule = Schedule.builder()
                .user(user)
                .name(name)
                .dailySchedules(dailySchedules)
                .isDefault(isDefault)
                .build();

        return scheduleRepository.save(schedule);
    }

    private DailySchedule buildDay(int dayOfWeek, LocalTime start, LocalTime end, boolean available) {
        return DailySchedule.builder()
                .dayOfWeek(dayOfWeek)
                .startTime(start)
                .endTime(end)
                .isAvailable(available)
                .build();
    }

    private ScheduleResponse toResponse(Schedule schedule) {
        return new ScheduleResponse(schedule);
    }
}
