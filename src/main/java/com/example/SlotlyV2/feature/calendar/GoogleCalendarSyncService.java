package com.example.SlotlyV2.feature.calendar;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;

import com.example.SlotlyV2.feature.booking.Booking;
import com.example.SlotlyV2.feature.calendar.dto.GoogleEventRequest;
import com.example.SlotlyV2.feature.calendar.enums.SyncStatus;
import com.example.SlotlyV2.feature.user.User;
import com.google.api.services.calendar.model.Event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarSyncService {
    private final GoogleCalendarService googleCalendarService;
    private final GoogleOAuth2Service googleOAuth2Service;
    private final BookingGoogleEventRepository bookingGoogleEventRepository;

    @Transactional
    public void syncSlot(Booking booking, User user) {
        if (!googleOAuth2Service.isConnectedAndValid(user.getId())) {
            log.debug("Google calendar not connected for user {}, skipping sync", user.getId());
            return;
        }

        BookingGoogleEvent mapping = bookingGoogleEventRepository.findByBookingId(booking.getId()).orElse(null);

        GoogleEventRequest request = buildGoogleEventRequest(booking);

        if (mapping == null) {
            try {
                Event event = googleCalendarService.createEvent(user, request);

                BookingGoogleEvent newMapping = new BookingGoogleEvent();
                newMapping.setBooking(booking);
                newMapping.markSynced(event.getId());
                bookingGoogleEventRepository.save(newMapping);
            } catch (Exception e) {
                log.error("Failed to sync slot {} to Google Calendar", booking.getId(), e);
            }

            return;
        }

        try {
            googleCalendarService.updateEvent(user, mapping.getGoogleEventId(), request);
            mapping.markSynced(mapping.getGoogleEventId());
        } catch (Exception e) {
            log.error("Failed to update Google Calendar event {} for slot {}",
                    mapping.getGoogleEventId(), booking.getId(), e);
            mapping.markFailed(e.getMessage());
        }

        bookingGoogleEventRepository.save(mapping);
    }

    @Transactional
    public void deleteGoogleEvent(Booking booking, User user) {
        BookingGoogleEvent mapping = bookingGoogleEventRepository.findByBookingId(booking.getId()).orElse(null);

        if (mapping == null) {
            log.debug("Google Calendar mapping not found for slot {}", booking.getId());
            return;
        }

        if (!googleOAuth2Service.isConnectedAndValid(user.getId())) {
            log.warn("Google calendar not connected for user {}, marking slot {} for deletion",
                    user.getId(), booking.getId());
            mapping.setSyncStatus(SyncStatus.PENDING_DELETION);
            mapping.setSyncedAt(OffsetDateTime.now());
            bookingGoogleEventRepository.save(mapping);
            return;
        }

        try {
            googleCalendarService.deleteEvent(user, mapping.getGoogleEventId());
            bookingGoogleEventRepository.delete(mapping);
        } catch (Exception e) {
            log.error("Failed to delete Google Calendar event {} for slot {}",
                    mapping.getGoogleEventId(), booking.getId(), e);
            mapping.markFailed(e.getMessage());
            bookingGoogleEventRepository.save(mapping);
        }
    }

    private GoogleEventRequest buildGoogleEventRequest(Booking booking) {
        String summary = new StringBuilder()
                .append(booking.getEvent().getEventName())
                .append(" with ")
                .append(booking.getEvent().getHost().getDisplayName())
                .toString();

        String description = new StringBuilder()
                .append("Booked via Slotly")
                .append("\nAttendee: ")
                .append(booking.getAttendeeName())
                .append("\nEmail: ")
                .append(booking.getAttendeeEmail())
                .toString();

        return GoogleEventRequest.builder()
                .summary(summary)
                .description(description)
                .startTime(booking.getSlot().getStartTime())
                .endTime(booking.getSlot().getEndTime())
                .build();
    }
}
