package com.example.SlotlyV2.feature.schedule.dto;

import java.util.List;
import java.util.UUID;

import com.example.SlotlyV2.feature.schedule.Schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {
    private UUID id;
    private String name;
    private List<DailyScheduleResponse> dailySchedules;
    private Boolean isDefault;

    public ScheduleResponse(Schedule schedule) {
        this.id = schedule.getId();
        this.name = schedule.getName();
        this.dailySchedules = schedule.getDailySchedules().stream()
                .map(DailyScheduleResponse::new)
                .toList();
        this.isDefault = schedule.getIsDefault();
    }
}
