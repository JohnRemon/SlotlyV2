package com.example.SlotlyV2.feature.availability;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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
    @Column(name = "slot_duration_minutes")
    @Builder.Default
    private Integer slotDurationMinutes = 30;

    @Column(name = "max_slots_per_user")
    @Builder.Default
    private Integer maxSlotsPerUser = 1;

    @Column(name = "allow_cancellation")
    @Builder.Default
    private boolean allowsCancellations = true;

    @Column(name = "is_public")
    @Builder.Default
    private boolean isPublic = true;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "buffer_minutes")
    @Builder.Default
    private Integer bufferMinutes = 0;
}
