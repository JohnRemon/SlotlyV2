package com.example.SlotlyV2.common.util;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TimeZoneConverter {

    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");

    public OffsetDateTime toUserTimezone(OffsetDateTime utcTime, String userTimezone) {
        ZoneId userZone = ZoneId.of(userTimezone);
        return utcTime.atZoneSameInstant(UTC_ZONE).withZoneSameInstant(userZone).toOffsetDateTime();
    }

    public OffsetDateTime toUtc(OffsetDateTime localTime, String userTimezone) {
        return localTime.atZoneSameInstant(ZoneId.of(userTimezone))
                .withZoneSameInstant(UTC_ZONE)
                .toOffsetDateTime();
    }
}
