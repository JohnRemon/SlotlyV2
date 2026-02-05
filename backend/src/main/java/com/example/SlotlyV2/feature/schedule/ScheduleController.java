package com.example.SlotlyV2.feature.schedule;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.feature.schedule.dto.BlockedPeriodRequest;
import com.example.SlotlyV2.feature.schedule.dto.BlockedPeriodResponse;
import com.example.SlotlyV2.feature.schedule.dto.DailyScheduleResponse;
import com.example.SlotlyV2.feature.schedule.dto.ScheduleRequest;
import com.example.SlotlyV2.feature.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final UserService userService;

    @PutMapping
    public ApiResponse<Void> updateSchedule(@Valid @RequestBody ScheduleRequest request) {
        scheduleService.updateSchedule(userService.getCurrentUser(), request);
        return new ApiResponse<>("Schedule updated successfully", null);
    }

    @PostMapping("/block-period")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> createBlockedPeriod(@Valid @RequestBody BlockedPeriodRequest request) {
        scheduleService.createBlockedPeriod(userService.getCurrentUser(), request);
        return new ApiResponse<>("Period added successfully", null);
    }

    @GetMapping
    public ApiResponse<List<DailyScheduleResponse>> getSchedule() {
        return new ApiResponse<>("Schedule fetched successfully",
                scheduleService.getSchedule(userService.getCurrentUser()));
    }

    @GetMapping("/block-periods")
    public ApiResponse<List<BlockedPeriodResponse>> getBlockedPeriods() {
        return new ApiResponse<>("Blocked periods fetched successfully",
                scheduleService.getBlockedPeriods(userService.getCurrentUser()));
    }
}
