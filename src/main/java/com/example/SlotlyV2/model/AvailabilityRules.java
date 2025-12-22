package com.example.SlotlyV2.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityRules {
    private Integer slotDurationMinutes = 30;
    private Integer maxSlotsPerUser = 1;
    private Boolean allowCancellations = true;
    private Boolean isPublic = true;
}
