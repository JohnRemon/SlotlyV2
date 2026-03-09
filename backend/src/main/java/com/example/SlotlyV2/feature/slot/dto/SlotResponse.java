package com.example.SlotlyV2.feature.slot.dto;

import java.time.OffsetDateTime;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.slot.Slot;

public class SlotResponse {
    private final Long id;
    private final OffsetDateTime startTime;
    private final OffsetDateTime endTime;

    public SlotResponse(Slot slot, TimeZoneConverter timeZoneConverter) {
        this.id = slot.getId();
        this.startTime = timeZoneConverter.toUtc(slot.getStartTime());
        this.endTime = timeZoneConverter.toUtc(slot.getEndTime());
    }
}
