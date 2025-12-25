package com.example.SlotlyV2.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.SlotlyV2.event.SlotBookedEvent;
import com.example.SlotlyV2.model.Slot;
import com.example.SlotlyV2.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailEventListener {
    private final EmailService emailService;

    @EventListener
    @Async("emailTaskExecutor")
    public void handleSlotBooked(SlotBookedEvent event) {
        Slot slot = event.getSlot();

        log.debug("Received SlotBookedEvent for slot: {}", slot.getId());

        try {
            emailService.sendBookingConfirmation(slot);
            emailService.sendHostNotification(slot);
            log.info("Emails sent successfully for slot: {}", slot.getId());
        } catch (Exception e) {
            log.error("Failed to send emails for slot {}: {}", slot.getId(), e.getMessage());
            // TODO add to retry queue
        }

    }

}
