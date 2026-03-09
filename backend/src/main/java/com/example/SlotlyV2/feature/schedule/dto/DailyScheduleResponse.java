package com.example.SlotlyV2.feature.schedule.dto;

import java.time.LocalTime;

import com.example.SlotlyV2.feature.schedule.DailySchedule;

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
