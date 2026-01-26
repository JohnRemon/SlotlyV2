package com.example.SlotlyV2.feature.calendar.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.SlotlyV2.common.exception.slot.SlotNotFoundException;
import com.example.SlotlyV2.common.exception.user.UserNotFoundException;
import com.example.SlotlyV2.feature.calendar.GoogleCalendarSyncService;
import com.example.SlotlyV2.feature.calendar.events.SlotBookedSyncEvent;
import com.example.SlotlyV2.feature.calendar.events.SlotCancelledSyncEvent;
import com.example.SlotlyV2.feature.slot.Slot;
import com.example.SlotlyV2.feature.slot.SlotRepository;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleSyncEventListener {
    private final GoogleCalendarSyncService googleCalendarSyncService;
    private final UserRepository userRepository;
    private final SlotRepository slotRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("calendarSyncExecutor")
    public void handleSlotBooked(SlotBookedSyncEvent event) {
        log.debug("Handling sync for slot {}", event.getCalendarSyncDataDTO().getSlotId());
        try {
            Slot slot = slotRepository.findById(event.getCalendarSyncDataDTO().getSlotId())
                    .orElseThrow(() -> new SlotNotFoundException("Slot not found"));
            User user = userRepository.findById(event.getCalendarSyncDataDTO().getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            googleCalendarSyncService.syncSlot(slot, user);
        } catch (Exception e) {
            log.error("Failed to handle sync for slot {}", event.getCalendarSyncDataDTO().getSlotId());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("calendarSyncExecutor")
    public void handleSlotCancelled(SlotCancelledSyncEvent event) {
        log.debug("Handling sync deletion for slot {}", event.getCalendarSyncDataDTO().getSlotId());
        try {
            Slot slot = slotRepository.findById(event.getCalendarSyncDataDTO().getSlotId())
                    .orElseThrow(() -> new SlotNotFoundException("Slot not found"));
            User user = userRepository.findById(event.getCalendarSyncDataDTO().getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            googleCalendarSyncService.deleteGoogleEvent(slot, user);
        } catch (Exception e) {
            log.error("Failed to handle sync for slot {}", event.getCalendarSyncDataDTO().getSlotId());
        }
    }
}
