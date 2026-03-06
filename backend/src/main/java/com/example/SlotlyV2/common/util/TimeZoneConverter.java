package com.example.SlotlyV2.common.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TimeZoneConverter {

    public OffsetDateTime toUtc(OffsetDateTime localTime) {
        return localTime.withOffsetSameInstant(ZoneOffset.UTC);
    }
}
