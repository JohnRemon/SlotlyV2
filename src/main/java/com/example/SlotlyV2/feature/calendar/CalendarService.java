package com.example.SlotlyV2.feature.calendar;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.util.NameUtils;
import com.example.SlotlyV2.feature.slot.Slot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {
    private final NameUtils nameUtils;

    private static final DateTimeFormatter ICS_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    public String generateIcsFile(Slot slot) {
        // Get event details
        String eventName = slot.getEvent().getEventName();
        String hostName = nameUtils.getUserDisplayName(slot);
        String hostEmail = slot.getEvent().getHost().getEmail();
        String attendeeName = slot.getBookedByName();
        String attendeeEmail = slot.getBookedByEmail();
        String timeZone = slot.getEvent().getTimeZone();

        // Convert times to UTC
        String startTimeUTC = convertToUTC(slot.getStartTime(), timeZone);
        String endTimeUTC = convertToUTC(slot.getEndTime(), timeZone);
        String timestampUTC = ZonedDateTime.now(ZoneId.of("UTC")).format(ICS_DATE_FORMAT);

        // Generate unique ID
        String uuid = slot.getId().toString();

        // Build the ICS file
        StringBuilder ics = new StringBuilder();

        ics.append("BEGIN:VCALENDAR\r\n");
        ics.append("VERSION:2.0\r\n");
        ics.append("PRODID:-//Slotly//Calendar//EN\r\n");
        ics.append("BEGIN:VEVENT\r\n");
        ics.append("UID:slot-" + uuid + "@slotly.com\r\n");
        ics.append("DTSTAMP:" + timestampUTC + "\r\n");
        ics.append("DTSTART:" + startTimeUTC + "\r\n");
        ics.append("DTEND:" + endTimeUTC + "\r\n");
        ics.append("SUMMARY:" + eventName + " with " + hostName + "\r\n");
        ics.append("DESCRIPTION:Your booking with " + hostName + "\r\n");
        ics.append("ORGANIZER;CN=" + hostName + ":mailto:" + hostEmail + "\r\n");
        ics.append("ATTENDEE;CN=" + attendeeName + ":mailto:" + attendeeEmail + "\r\n");
        ics.append("END:VEVENT\r\n");
        ics.append("END:VCALENDAR\r\n");

        log.debug("Generated ICS for slot {}", slot.getId());

        return ics.toString();
    }

    private String convertToUTC(LocalDateTime localDateTime, String timeZoneId) {
        ZoneId zoneId = ZoneId.of(timeZoneId);

        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

        ZonedDateTime utcTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));

        return utcTime.format(ICS_DATE_FORMAT);
    }
}
