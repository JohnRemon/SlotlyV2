package com.example.SlotlyV2.feature.schedule.dto;

import java.util.List;

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
    private List<DailyScheduleResponse> dailyScheduleResponses;

    public ScheduleResponse(Schedule schedule) {
        this.dailyScheduleResponses = schedule.getDailySchedules().stream()
                .map(DailyScheduleResponse::new)
                .toList();
    }
}
