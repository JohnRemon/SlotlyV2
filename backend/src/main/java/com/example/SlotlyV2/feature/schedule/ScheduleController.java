package com.example.SlotlyV2.feature.schedule;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.DataResponse;
import com.example.SlotlyV2.common.dto.PagedResponse;
import com.example.SlotlyV2.feature.schedule.dto.ScheduleRequest;
import com.example.SlotlyV2.feature.schedule.dto.ScheduleResponse;
import com.example.SlotlyV2.feature.schedule.dto.UpdateScheduleRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public PagedResponse<ScheduleResponse> getSchedules(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return PagedResponse.of(scheduleService.getSchedules(pageable));
    }

    @GetMapping("/{id}")
    public DataResponse<ScheduleResponse> getScheduleById(@PathVariable UUID id) {
        return DataResponse.of(scheduleService.getSchedule(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse<ScheduleResponse> createSchedule(@Valid @RequestBody ScheduleRequest request) {
        return DataResponse.of(scheduleService.createSchedule(request));
    }

    @PutMapping("/{id}")
    public DataResponse<ScheduleResponse> updateSchedule(
            @Valid @RequestBody UpdateScheduleRequest request,
            @PathVariable UUID id) {
        return DataResponse.of(scheduleService.updateSchedule(request, id));
    }

    @PatchMapping("/{id}/name")
    public DataResponse<ScheduleResponse> updateScheduleName(
            @PathVariable UUID id,
            @RequestParam String name) {
        return DataResponse.of(scheduleService.updateScheduleName(name, id));
    }

    @PatchMapping("/{id}/default")
    public DataResponse<ScheduleResponse> setDefaultSchedule(@PathVariable UUID id) {
        return DataResponse.of(scheduleService.updateDefaultSchedule(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSchedule(@PathVariable UUID id) {
        scheduleService.deleteSchedule(id);
    }
}
