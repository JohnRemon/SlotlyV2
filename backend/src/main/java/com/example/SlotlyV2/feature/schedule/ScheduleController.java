package com.example.SlotlyV2.feature.schedule;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.feature.schedule.dto.BlockedPeriodRequest;
import com.example.SlotlyV2.feature.schedule.dto.BlockedPeriodResponse;
import com.example.SlotlyV2.feature.schedule.dto.ScheduleRequest;
import com.example.SlotlyV2.feature.schedule.dto.ScheduleResponse;
import com.example.SlotlyV2.feature.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final UserService userService;

    @GetMapping("/{id}")
    public ApiResponse<ScheduleResponse> getScheduleById(@PathVariable UUID id) {
        Schedule schedule = scheduleService.getSchedule(id);
        return new ApiResponse<>("Schedule fetched successfully", new ScheduleResponse(schedule));
    }

    @GetMapping
    public ApiResponse<List<ScheduleResponse>> getSchedules() {
        List<Schedule> schedules = scheduleService.getSchedules(userService.getCurrentUser());

        List<ScheduleResponse> scheduleResponses = schedules.stream()
                .map(ScheduleResponse::new)
                .toList();

        return new ApiResponse<>("Schedules fetched successfully", scheduleResponses);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ScheduleResponse> createSchedule(@Valid @RequestBody ScheduleRequest request) {
        Schedule schedule = scheduleService.createSchedule(userService.getCurrentUser(), request);
        return new ApiResponse<>("Schedule created successfully", new ScheduleResponse(schedule));
    }

    @PutMapping("/{id}")
    public ApiResponse<ScheduleResponse> updateSchedule(@Valid @RequestBody ScheduleRequest request,
            @PathVariable UUID id) {
        Schedule schedule = scheduleService.updateSchedule(request, id);
        return new ApiResponse<>("Schedule updated successfully", new ScheduleResponse(schedule));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSchedule(@PathVariable UUID id) {
        scheduleService.deleteSchedule(id);
        return new ApiResponse<>("Schedule deleted successfully", null);
    }

    @GetMapping("/blocked-periods/{id}")
    public ApiResponse<BlockedPeriodResponse> getBlockedPeriodById(@PathVariable UUID id) {
        BlockedPeriod blockedPeriod = scheduleService.getBookingPeriodById(id);
        return new ApiResponse<>("Blocked Period fetched successfully", new BlockedPeriodResponse(blockedPeriod));
    }

    @GetMapping("/block-periods")
    public ApiResponse<List<BlockedPeriodResponse>> getBlockedPeriods() {
        List<BlockedPeriod> blockedPeriods = scheduleService.getBlockedPeriods(userService.getCurrentUser());

        List<BlockedPeriodResponse> blockedPeriodResponses = blockedPeriods.stream()
                .map(BlockedPeriodResponse::new)
                .toList();

        return new ApiResponse<>("Blocked periods fetched successfully", blockedPeriodResponses);
    }

    @PostMapping("/block-period")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BlockedPeriodResponse> createBlockedPeriod(@Valid @RequestBody BlockedPeriodRequest request) {
        BlockedPeriod blockedPeriod = scheduleService.createBlockedPeriod(userService.getCurrentUser(), request);
        return new ApiResponse<>("Period added successfully", new BlockedPeriodResponse(blockedPeriod));
    }
}
