package com.example.SlotlyV2.feature.slot.dto;

import java.time.OffsetDateTime;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.slot.Slot;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class SlotResponse {
    @JsonProperty(index = 0)
    private final Long id;
    @JsonProperty(index = 5)
    private final OffsetDateTime startTime;
    @JsonProperty(index = 10)
    private final OffsetDateTime endTime;

    public SlotResponse(Slot slot, TimeZoneConverter timeZoneConverter, String timeZone) {
        this.id = slot.getId();
        this.startTime = timeZoneConverter.toTimezone(slot.getStartTime(), timeZone);
        this.endTime = timeZoneConverter.toTimezone(slot.getEndTime(), timeZone);
    }
}
