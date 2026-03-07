package com.example.SlotlyV2.feature.availability.dto;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class AvailabilityRulesUpdateRequest {
    private String eventName;
    private String description;
    private OffsetDateTime eventStart;
    private OffsetDateTime eventEnd;
    private Integer slotDurationMinutes;
    private Integer bufferMinutes;
    private Integer minimumNoticeHours;
    private Integer maximumAdvanceDays;
    private Integer maxCapacity;
    private Integer maxSlotsPerUser;
    private Boolean allowCancellations;
    private Boolean isPublic;
}
