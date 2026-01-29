package com.example.SlotlyV2.feature.availability.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityRulesDTO {

    @Min(value = 5, message = "Slot duration must be at least 5 minutes")
    private Integer slotDurationMinutes;

    @Min(value = 1, message = "Must allow at least 1 slot per user")
    private Integer maxSlotsPerUser;

    @Min(value = 0, message = "Buffer time can't be negative")
    private Integer bufferMinutes;

    @Min(value = 0, message = "Minimum notice hours can't be negative")
    private Integer minimumNoticeHours;

    @Min(value = 1, message = "Maximum advance days must be at least 1")
    private Integer maximumAdvanceDays;

    @Min(value = 1, message = "Max capacity must be greater than 1")
    private Integer maxCapacity;

    private Boolean allowCancellations;

    private Boolean isPublic;
}
