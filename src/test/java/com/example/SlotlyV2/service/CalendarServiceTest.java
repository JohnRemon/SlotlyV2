package com.example.SlotlyV2.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.SlotlyV2.feature.availability.AvailabilityRules;
import com.example.SlotlyV2.feature.calendar.CalendarService;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.slot.Slot;
import com.example.SlotlyV2.feature.user.User;

@ExtendWith(MockitoExtension.class)
public class CalendarServiceTest {

    @InjectMocks
    private CalendarService calendarService;

    @Test
    void shouldGenerateICSCalendarSuccessfully() {
        // Arrange
        Slot slot = createTestSlot();

        // Act
        String ics = calendarService.generateIcsFile(slot);

        // Assert - Basic ICS Structure
        assertTrue(ics.contains("BEGIN:VCALENDAR"));
        assertTrue(ics.contains("VERSION:2.0"));
        assertTrue(ics.contains("PRODID:-//Slotly//Calendar//EN"));
        assertTrue(ics.contains("BEGIN:VEVENT"));
        assertTrue(ics.contains("END:VEVENT"));
        assertTrue(ics.contains("END:VCALENDAR"));

        // Assert - Event Details
        assertTrue(ics.contains("UID:slot-1@slotly.com"));
        assertTrue(ics.contains("SUMMARY:Test Meeting with John Doe"));
        assertTrue(ics.contains("DESCRIPTION:Your booking with John Doe"));
        assertTrue(ics.contains("ORGANIZER;CN=John Doe:mailto:host@example.com"));
        assertTrue(ics.contains("ATTENDEE;CN=Jane Smith:mailto:jane@example.com"));

        // Assert - Time Formatting
        assertTrue(ics.matches("(?s).*DTSTART:\\d{8}T\\d{6}Z.*"));
        assertTrue(ics.matches("(?s).*DTEND:\\d{8}T\\d{6}Z.*"));
        assertTrue(ics.matches("(?s).*DTSTAMP:\\d{8}T\\d{6}Z.*"));
    }

    @Test
    void shouldHandleMissingAttendeeInformation() {
        // Arrange
        Slot slot = createTestSlot();
        slot.setBookedByName(null);
        slot.setBookedByEmail(null);

        // Act
        String ics = calendarService.generateIcsFile(slot);

        // Assert
        assertTrue(ics.contains("ATTENDEE;CN=null:mailto:null"));
    }

    @Test
    void shouldHandleHostWithOnlyFirstName() {
        // Arrange
        Slot slot = createTestSlot();
        slot.getEvent().getHost().setLastName(null);

        // Act
        String ics = calendarService.generateIcsFile(slot);

        // Assert
        assertTrue(ics.contains("ORGANIZER;CN=John:mailto:host@example.com"));
        assertTrue(ics.contains("SUMMARY:Test Meeting with John"));
    }

    @Test
    void shouldHandleHostWithOnlyLastName() {
        // Arrange
        Slot slot = createTestSlot();
        slot.getEvent().getHost().setFirstName(null);

        // Act
        String ics = calendarService.generateIcsFile(slot);

        // Assert
        assertTrue(ics.contains("ORGANIZER;CN=Doe:mailto:host@example.com"));
        assertTrue(ics.contains("SUMMARY:Test Meeting with Doe"));
    }

    @Test
    void shouldHandleHostWithNoNames() {
        // Arrange
        Slot slot = createTestSlot();
        slot.getEvent().getHost().setFirstName(null);
        slot.getEvent().getHost().setLastName(null);

        // Act
        String ics = calendarService.generateIcsFile(slot);

        // Assert
        assertTrue(ics.contains("ORGANIZER;CN=:mailto:host@example.com"));
        assertTrue(ics.contains("SUMMARY:Test Meeting with "));
    }

    private Slot createTestSlot() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("host@example.com");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");

        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(60);
        rules.setMaxSlotsPerUser(2);
        rules.setAllowsCancellations(true);
        rules.setIsPublic(true);

        Event event = new Event();
        event.setId(1L);
        event.setEventName("Test Meeting");
        event.setHost(mockUser);
        event.setRules(rules);
        event.setTimeZone("Europe/Berlin");

        Slot slot = new Slot();
        slot.setId(1L);
        slot.setEvent(event);
        slot.setStartTime(LocalDateTime.of(2024, 1, 15, 10, 0));
        slot.setEndTime(LocalDateTime.of(2024, 1, 15, 11, 0));
        slot.setBookedByName("Jane Smith");
        slot.setBookedByEmail("jane@example.com");

        return slot;
    }
}
