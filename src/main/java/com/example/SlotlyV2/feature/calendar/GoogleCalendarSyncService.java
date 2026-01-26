package com.example.SlotlyV2.feature.calendar;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

import com.example.SlotlyV2.feature.calendar.dto.GoogleEventRequest;
import com.example.SlotlyV2.feature.calendar.enums.SyncStatus;
import com.example.SlotlyV2.feature.slot.Slot;
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
    private final SlotGoogleEventRepository slotGoogleEventRepository;

    @Transactional
    public void syncSlot(Slot slot, User user) {

        if (!googleOAuth2Service.isConnected(user.getId())) {
            log.debug("Google calendar not connected for user {}, skipping sync", user.getId());
            return;
        }

        SlotGoogleEvent mapping = slotGoogleEventRepository.findBySlotId(slot.getId()).orElse(null);

        GoogleEventRequest request = buildGoogleEventRequest(slot);

        if (mapping == null) {
            try {
                Event event = googleCalendarService.createEvent(user, request);

                SlotGoogleEvent newMapping = new SlotGoogleEvent();
                newMapping.setSlot(slot);
                newMapping.markSynced(event.getId());
                slotGoogleEventRepository.save(newMapping);
            } catch (Exception e) {
                log.error("Failed to sync slot {} to Google Calendar", slot.getId(), e);
            }

            return;
        }

        try {
            googleCalendarService.updateEvent(user, mapping.getGoogleEventId(), request);
            mapping.markSynced(mapping.getGoogleEventId());
        } catch (Exception e) {
            log.error("Failed to update Google Calendar event {} for slot {}",
                    mapping.getGoogleEventId(), slot.getId(), e);
            mapping.markFailed(e.getMessage());
        }

        slotGoogleEventRepository.save(mapping);
    }

    @Transactional
    public void deleteGoogleEvent(Slot slot, User user) {
        SlotGoogleEvent mapping = slotGoogleEventRepository.findBySlotId(slot.getId()).orElse(null);

        if (mapping == null) {
            log.debug("Google Calendar mapping not found for slot {}", slot.getId());
            return;
        }

        if (!googleOAuth2Service.isConnected(user.getId())) {
            log.warn("Google calendar not connected for user {}, marking slot {} for deletion",
                    user.getId(), slot.getId());
            mapping.setSyncStatus(SyncStatus.PENDING_DELETION);
            mapping.setSyncedAt(OffsetDateTime.now());
            slotGoogleEventRepository.save(mapping);
            return;
        }

        try {
            googleCalendarService.deleteEvent(user, mapping.getGoogleEventId());
            slotGoogleEventRepository.delete(mapping);
        } catch (Exception e) {
            log.error("Failed to delete Google Calendar event {} for slot {}",
                    mapping.getGoogleEventId(), slot.getId(), e);
            mapping.markFailed(e.getMessage());
            slotGoogleEventRepository.save(mapping);
        }
    }

    private GoogleEventRequest buildGoogleEventRequest(Slot slot) {
        String summary = new StringBuilder()
                .append(slot.getEvent().getEventName())
                .append(" with ")
                .append(slot.getEvent().getHost().getDisplayName())
                .toString();

        String description = new StringBuilder()
                .append("Booked via Slotly")
                .append("\nAttendee: ")
                .append(slot.getBookedByName())
                .append("\nEmail: ")
                .append(slot.getBookedByEmail())
                .toString();

        return GoogleEventRequest.builder()
                .summary(summary)
                .description(description)
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .build();
    }
}
