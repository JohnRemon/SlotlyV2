package com.example.SlotlyV2.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.SlotlyV2.dto.BookingEmailDTO;
import com.example.SlotlyV2.dto.EventCancelledEmailDTO;
import com.example.SlotlyV2.dto.PasswordResetDTO;
import com.example.SlotlyV2.dto.SlotCancelledEmailDTO;
import com.example.SlotlyV2.dto.UserVerificationDTO;
import com.example.SlotlyV2.event.EmailVerificationEvent;
import com.example.SlotlyV2.event.EventCancelledEvent;
import com.example.SlotlyV2.event.PasswordResetEvent;
import com.example.SlotlyV2.event.SlotBookedEvent;
import com.example.SlotlyV2.event.SlotCancelledEvent;
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
        BookingEmailDTO data = event.getBookingEmailDTO();

        log.debug("Received SlotBookedEvent for slot: {}", data.getSlotId());

        try {
            emailService.sendBookingConfirmation(data);
            emailService.sendHostNotification(data);
            log.info("Emails sent successfully for slot: {}", data.getSlotId());
        } catch (Exception e) {
            log.error("Failed to send emails for slot {}: {}", data.getSlotId(), e.getMessage(), e);
        }
    }

    @EventListener
    @Async("emailTaskExecutor")
    public void handleSlotCancelled(SlotCancelledEvent event) {
        SlotCancelledEmailDTO data = event.getSlotCancelledEmailDTO();

        log.debug("Received SlotCancelledEvent for slot: {}", data.getSlotId());

        try {
            emailService.sendCancellationConfirmation(data);
            emailService.sendHostCancellationNotification(data);
            log.info("Cancellation emails sent successfully for slot: {}", data.getSlotId());
        } catch (Exception e) {
            log.error("Failed to send cancellation emails for slot {}: {}", data.getSlotId(), e.getMessage(), e);
        }
    }

    @EventListener
    @Async("emailTaskExecutor")
    public void handleEventCancelled(EventCancelledEvent event) {
        EventCancelledEmailDTO data = event.getEventCancelledEmailDTO();

        log.debug("Received EventCancelledEvent for event: {}", data.getEventId());

        try {
            emailService.sendEventCancellationNotifications(data);
            log.info("Event cancellation emails sent successfully for event: {}", data.getEventId());
        } catch (Exception e) {
            log.error("Failed to send event cancellation emails for event {}: {}", data.getEventId(), e.getMessage(),
                    e);
        }
    }

    @EventListener
    @Async("emailTaskExecutor")
    public void handleEmailVerification(EmailVerificationEvent event) {
        UserVerificationDTO data = event.getUserVerificationDTO();

        log.debug("Received EmailVerificationEvent for email: {}", data.getEmail());

        try {
            emailService.sendUserRegistrationVerification(data);
        } catch (Exception e) {
            log.error("Failed to send email for email {}: {}", data.getEmail(), e.getMessage(), e);
        }
    }

    @EventListener
    @Async("emailTaskExecutor")
    public void handleResetPassword(PasswordResetEvent event) {
        PasswordResetDTO data = event.getPasswordResetDTO();

        log.debug("Received PasswordResetEvent for email: {}", data.getEmail());

        try {
            emailService.sendPasswordRequest(data);
        } catch (Exception e) {
            log.error("Failed to send password reset email for email {}: {}", data.getEmail(), e.getMessage(), e);
        }
    }

}
