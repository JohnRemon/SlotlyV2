package com.example.SlotlyV2.common.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TimeZoneConverter {

    public LocalDateTime toUserTimezone(OffsetDateTime utcTime, String userTimezone) {
        ZoneId userZone = ZoneId.of(userTimezone);
        return utcTime.atZoneSameInstant(userZone)
                .toLocalDateTime();
    }

    public OffsetDateTime toUtc(LocalDateTime localTime, String userTimezone) {
        return localTime.atZone(ZoneId.of(userTimezone))
                .withZoneSameInstant(ZoneId.of("UTC"))
                .toOffsetDateTime();
    }
}
