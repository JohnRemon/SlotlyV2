package com.example.SlotlyV2.dto;

import com.example.SlotlyV2.model.AvailabilityRules;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AvailabilityRulesDTO {

    @NotNull
    @Min(value = 5, message = "Slot duration must be at least 5 minutes")
    private Integer slotDurationMinutes = 30;

    @NotNull
    @Min(value = 1, message = "Must allow at least 1 slot pers user")
    private Integer maxSlotsPerUser = 1;

    @NotNull
    private Boolean allowsCancellations = true;

    @NotNull
    private Boolean isPublic = true;

    public AvailabilityRulesDTO(AvailabilityRules rules) {
        this.slotDurationMinutes = rules.getSlotDurationMinutes();
        this.maxSlotsPerUser = rules.getMaxSlotsPerUser();
        this.allowsCancellations = rules.getAllowsCancellations();
        this.isPublic = rules.getIsPublic();
    }
}
