package com.example.SlotlyV2.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.SlotlyV2.dto.BookingEmailData;
import com.example.SlotlyV2.dto.PasswordResetData;
import com.example.SlotlyV2.dto.UserRegistrationVerificationData;
import com.example.SlotlyV2.event.EmailVerificationEvent;
import com.example.SlotlyV2.event.PasswordResetEvent;
import com.example.SlotlyV2.event.SlotBookedEvent;
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
        BookingEmailData data = event.getBookingData();

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
    public void handleEmailVerification(EmailVerificationEvent event) {
        UserRegistrationVerificationData data = event.getUserRegistrationVerificationData();

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
        PasswordResetData data = event.getPasswordResetData();

        log.debug("Received PasswordResetEvent for email: {}", data.getEmail());

        try {
            emailService.sendPasswordRequest(data);
        } catch (Exception e) {
            log.error("Failed to send password reset email for email {}: {}", data.getEmail(), e.getMessage(), e);
        }
    }

}
