package com.example.SlotlyV2.feature.availability;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityRules {
    @Builder.Default
    @Min(5)
    private Integer slotDurationMinutes = 30;

    @Builder.Default
    private Integer maxSlotsPerUser = 1;

    @Builder.Default
    private boolean allowsCancellations = true;

    @Builder.Default
    private boolean isPublic = true;

    @Min(1)
    private Integer maxCapacity;
}
