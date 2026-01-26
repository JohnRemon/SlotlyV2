package com.example.SlotlyV2.feature.calendar;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.SlotlyV2.feature.calendar.dto.GoogleEventRequest;
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
    @Async("calendarSyncExecutor")
    public void syncSlot(Slot slot, User user) {

        if (!googleOAuth2Service.isConnected(user.getId())) {
            log.debug("Google calendar not connected for user {}, skipping sync", user.getId());
            return;
        }

        SlotGoogleEvent mapping = slotGoogleEventRepository.findBySlotId(slot.getId()).orElse(null);

        GoogleEventRequest request = buildGoogleEventRequest(slot);

        if (mapping == null) {
            Event event = googleCalendarService.createEvent(user, request);

            SlotGoogleEvent newMapping = new SlotGoogleEvent();
            newMapping.setSlot(slot);
            newMapping.markSynced(event.getId());
            slotGoogleEventRepository.save(newMapping);
        } else {
            googleCalendarService.updateEvent(user, mapping.getGoogleEventId(), request);
            mapping.markSynced(mapping.getGoogleEventId());
            slotGoogleEventRepository.save(mapping);
        }
    }

    @Transactional
    @Async("calendarSyncExecutor")
    public void deleteGoogleEvent(Slot slot, User user) {
        SlotGoogleEvent mapping = slotGoogleEventRepository.findBySlotId(slot.getId()).orElse(null);

        if (mapping == null) {
            log.debug("slot not found");
            return;
        }

        googleCalendarService.deleteEvent(user, mapping.getGoogleEventId());
        slotGoogleEventRepository.delete(mapping);
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
