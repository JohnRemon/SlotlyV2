package com.example.SlotlyV2.feature.slot.dto;

import java.time.OffsetDateTime;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.slot.Slot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotResponse {
    private Long id;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;

    public SlotResponse(Slot slot, TimeZoneConverter timeZoneConverter) {
        this.id = slot.getId();
        this.startTime = timeZoneConverter.toUtc(slot.getStartTime());
        this.endTime = timeZoneConverter.toUtc(slot.getEndTime());
    }
}
