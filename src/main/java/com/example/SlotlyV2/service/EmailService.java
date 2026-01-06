package com.example.SlotlyV2.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.example.SlotlyV2.config.EmailConfig;
import com.example.SlotlyV2.dto.BookingEmailDTO;
import com.example.SlotlyV2.dto.PasswordResetDTO;
import com.example.SlotlyV2.dto.SlotCancelledEmailDTO;
import com.example.SlotlyV2.dto.UserVerificationDTO;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final SpringTemplateEngine templateEngine;
    private final EmailConfig emailConfig;
    private final Resend resend;
    @Value("${app.base-url}")
    private String appBaseUrl;

    @Async("emailTaskExecutor")
    public void sendBookingConfirmation(BookingEmailDTO data) {
        try {
            log.info("Sending booking confirmation to: {}", data.getToEmail());

            Map<String, Object> fields = new HashMap<>();
            fields.put("hostName", data.getHostDisplayName());
            fields.put("hostEmail", data.getHostEmail());
            fields.put("attendeeName", data.getAttendeeName());
            fields.put("eventName", data.getEventName());
            fields.put("startTime", data.getStartTime());
            fields.put("endTime", data.getEndTime());
            fields.put("date", data.getStartTime());
            fields.put("timeZone", data.getTimeZone());
            fields.put("calendarLink", appBaseUrl + "/api/v1/calendar/" + data.getSlotId());

            String htmlContent = renderTemplate("email/booking-confirmation", fields);

            sendEmail(data.getToEmail(),
                    "Booking Confirmed: " + data.getEventName(),
                    htmlContent);

            log.info("Booking confirmation sent successfully to: {}", data.getToEmail());
        } catch (Exception e) {
            log.error("Failed to send booking confirmation for slot {}: {}",
                    data.getSlotId(), e.getMessage(), e);
        }
    }

    @Async("emailTaskExecutor")
    public void sendHostNotification(BookingEmailDTO data) {
        log.info("Sending booking notification to: {}", data.getHostEmail());

        try {
            Map<String, Object> fields = new HashMap<>();
            fields.put("hostName", data.getHostDisplayName());
            fields.put("attendeeName", data.getAttendeeName());
            fields.put("attendeeEmail", data.getAttendeeEmail());
            fields.put("eventName", data.getEventName());
            fields.put("startTime", data.getStartTime());
            fields.put("endTime", data.getEndTime());
            fields.put("date", data.getStartTime());
            fields.put("timeZone", data.getTimeZone());

            String htmlContent = renderTemplate("email/booking-notification", fields);

            sendEmail(
                    data.getHostEmail(),
                    "New Booking: " + data.getAttendeeName(),
                    htmlContent);

            log.info("Host notification sent successfully for slot {}", data.getSlotId());
        } catch (Exception e) {
            log.error("Failed to send host notification for slot {}: {}",
                    data.getSlotId(), e.getMessage(), e);
        }

    }

    @Async("emailTaskExecutor")
    public void sendUserRegistrationVerification(UserVerificationDTO data) {
        log.info("Sending regsitration verification to: {}", data.getEmail());

        try {
            Map<String, Object> fields = new HashMap<>();
            fields.put("displayName", data.getDisplayName());
            fields.put("verificationLink", appBaseUrl + "/api/v1/users/verify-email?token=" + data.getToken());

            String htmlContent = renderTemplate("email/email-verification", fields);

            sendEmail(
                    data.getEmail(),
                    "Please verify your account",
                    htmlContent);

            log.info("Verification email sent successfully to: {}", data.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email for user: {}", data.getEmail());
        }
    }

    @Async("emailTaskExecutor")
    public void sendPasswordRequest(PasswordResetDTO data) {
        log.info("Sending Password Reset Email to: {}", data.getEmail());

        try {
            Map<String, Object> fields = new HashMap<>();
            fields.put("displayName", data.getDisplayName());
            fields.put("passwordResetLink",
                    appBaseUrl + "/api/v1/users/reset-password/confirm?token=" + data.getToken());

            String htmlContent = renderTemplate("email/reset-password", fields);

            sendEmail(
                    data.getEmail(),
                    "Reset your password",
                    htmlContent);

            log.info("Password reset email sent successfully to: {}", data.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email for user: {}", data.getEmail());
        }
    }

    @Async("emailTaskExecutor")
    public void sendCancellationConfirmation(SlotCancelledEmailDTO data) {
        log.info("Sending cancellation confirmation to: {}", data.getBookedByEmail());

        try {
            Map<String, Object> fields = new HashMap<>();
            fields.put("hostName", data.getHostName());
            fields.put("attendeeName", data.getBookedByName());
            fields.put("eventName", data.getEventName());
            fields.put("startTime", data.getSlotStartTime());
            fields.put("endTime", data.getSlotEndTime());

            String htmlContent = renderTemplate("email/cancellation-confirmation", fields);

            sendEmail(
                    data.getBookedByEmail(),
                    "Booking Cancelled: " + data.getEventName(),
                    htmlContent);

            log.info("Cancellation confirmation sent successfully to: {}", data.getBookedByEmail());
        } catch (Exception e) {
            log.error("Failed to send cancellation confirmation for slot {}: {}",
                    data.getSlotId(), e.getMessage(), e);
        }
    }

    @Async("emailTaskExecutor")
    public void sendHostCancellationNotification(SlotCancelledEmailDTO data) {
        log.info("Sending cancellation notification to: {}", data.getHostEmail());

        try {
            Map<String, Object> fields = new HashMap<>();
            fields.put("hostName", data.getHostName());
            fields.put("attendeeName", data.getBookedByName());
            fields.put("attendeeEmail", data.getBookedByEmail());
            fields.put("eventName", data.getEventName());
            fields.put("startTime", data.getSlotStartTime());
            fields.put("endTime", data.getSlotEndTime());

            String htmlContent = renderTemplate("email/cancellation-notification", fields);

            sendEmail(
                    data.getHostEmail(),
                    "Booking Cancelled: " + data.getBookedByName(),
                    htmlContent);

            log.info("Host cancellation notification sent successfully for slot {}", data.getSlotId());
        } catch (Exception e) {
            log.error("Failed to send host cancellation notification for slot {}: {}",
                    data.getSlotId(), e.getMessage(), e);
        }
    }

    public void sendEmail(String to, String subject, String htmlContent) throws ResendException {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(emailConfig.getFromName() + " <" + emailConfig.getFromEmail() + ">")
                .to(to)
                .subject(subject)
                .html(htmlContent)
                .build();

        resend.emails().send(params);
    }

    private String renderTemplate(String templateName, Map<String, Object> fields) {
        Context context = new Context();
        context.setVariables(fields);
        return templateEngine.process(templateName, context);
    }

}
