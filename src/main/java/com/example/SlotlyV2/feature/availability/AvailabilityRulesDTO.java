package com.example.SlotlyV2.feature.availability;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
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
    private Boolean allowsCancellations = true;

    @NotNull
    @Builder.Default
    private Boolean isPublic = true;
}
