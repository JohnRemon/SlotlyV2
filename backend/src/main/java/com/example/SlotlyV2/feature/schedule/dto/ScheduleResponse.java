package com.example.SlotlyV2.feature.schedule.dto;

import java.util.List;
import java.util.UUID;

import com.example.SlotlyV2.feature.schedule.Schedule;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class ScheduleResponse {
    @JsonProperty(index = 0)
    private UUID id;

    @JsonProperty(index = 5)
    private String name;

    @JsonProperty(index = 10)
    private Boolean isDefault;

    @JsonProperty(index = 15)
    private List<DailyScheduleResponse> dailySchedules;

    public ScheduleResponse(Schedule schedule) {
        this.id = schedule.getId();
        this.name = schedule.getName();
        this.dailySchedules = schedule.getDailySchedules().stream()
                .map(DailyScheduleResponse::new)
                .toList();
        this.isDefault = schedule.getIsDefault();
    }
}
