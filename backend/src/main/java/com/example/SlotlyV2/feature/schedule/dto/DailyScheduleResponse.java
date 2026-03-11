package com.example.SlotlyV2.feature.schedule.dto;

import java.time.LocalTime;

import com.example.SlotlyV2.feature.schedule.DailySchedule;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class DailyScheduleResponse {
    @JsonProperty(index = 0)
    private Integer dayOfWeek;

    @JsonProperty(index = 5)
    private LocalTime startTime;

    @JsonProperty(index = 10)
    private LocalTime endTime;

    @JsonProperty(index = 15)
    private Boolean isAvailable;

    public DailyScheduleResponse(DailySchedule dailySchedule) {
        this.dayOfWeek = dailySchedule.getDayOfWeek();
        this.startTime = dailySchedule.getStartTime();
        this.endTime = dailySchedule.getEndTime();
        this.isAvailable = dailySchedule.isAvailable();
    }
}
