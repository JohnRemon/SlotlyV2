package com.example.SlotlyV2.common.util;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TimeZoneConverter {

    public OffsetDateTime toUtc(OffsetDateTime localTime) {
        return localTime.withOffsetSameInstant(ZoneOffset.UTC);
    }

    public OffsetDateTime toTimezone(OffsetDateTime utcTime, String timeZone) {
        return utcTime
                .atZoneSameInstant(ZoneId.of(timeZone))
                .toOffsetDateTime();
    }
}
