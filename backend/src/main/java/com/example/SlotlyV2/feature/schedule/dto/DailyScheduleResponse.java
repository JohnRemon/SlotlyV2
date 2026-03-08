package com.example.SlotlyV2.feature.schedule.dto;

import java.time.LocalTime;

import com.example.SlotlyV2.feature.schedule.DailySchedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class DailyScheduleResponse {
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isAvailable;

    public DailyScheduleResponse(DailySchedule dailySchedule) {
        this.dayOfWeek = dailySchedule.getDayOfWeek();
        this.startTime = dailySchedule.getStartTime();
        this.endTime = dailySchedule.getEndTime();
        this.isAvailable = dailySchedule.isAvailable();
    }
}
