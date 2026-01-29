package com.example.SlotlyV2.feature.availability.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityRulesDTO {
    @NotNull
    @Min(value = 5, message = "Slot duration must be at least 5 minutes")
    @Builder.Default
    private Integer slotDurationMinutes = 30;

    @NotNull
    @Min(value = 1, message = "Must allow at least 1 slot pers user")
    @Builder.Default
    private Integer maxSlotsPerUser = 1;

    @NotNull
    @Builder.Default
    private boolean allowCancellations = true;

    @NotNull
    @Builder.Default
    private boolean isPublic = true;

    @Min(value = 1, message = "Max capacity must be greater than 1")
    private Integer maxCapacity;

    @Min(value = 0, message = "Buffer time can't be negative")
    @Builder.Default
    private Integer bufferMinutes = 0;

    @Min(value = 0, message = "minimum notice hours can't be negative")
    @Builder.Default
    private Integer minimumNoticeHours = 0;

    @Min(value = 1, message = "maximum advance days must be at least 1")
    @Builder.Default
    private Integer maximumAdvanceDays = 90;
}
