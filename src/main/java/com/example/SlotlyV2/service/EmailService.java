package com.example.SlotlyV2.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.example.SlotlyV2.config.EmailConfig;
import com.example.SlotlyV2.model.Slot;
import com.example.SlotlyV2.model.User;
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

    @Async("emailTaskExecutor")
    public void sendBookingConfirmation(Slot slot) {
        try {
            log.info("Sending booking confirmation to: {}", slot.getBookedByEmail());

            Map<String, Object> fields = new HashMap<>();
            fields.put("hostName", getHostDisplayName(slot));
            fields.put("hostEmail", slot.getEvent().getHost().getEmail());
            fields.put("attendeeName", slot.getBookedByName());
            fields.put("eventName", slot.getEvent().getEventName());
            fields.put("startTime", slot.getStartTime());
            fields.put("endTime", slot.getEndTime());
            fields.put("date", slot.getStartTime());
            fields.put("timeZone", slot.getEvent().getTimeZone());

            String htmlContent = renderTemplate("email/booking-confirmation", fields);

            sendEmail(slot.getBookedByEmail(),
                    "Booking Confirmed: " + slot.getEvent().getEventName(),
                    htmlContent);

            log.info("Booking confirmation sent successfully to: {}", slot.getBookedByEmail());
        } catch (Exception e) {
            log.error("Failed to send booking confirmation for slot {}: {}",
                    slot.getId(), e.getMessage(), e);

            // TODO add in retry queue
        }
    }

    @Async("emailTaskExecutor")
    public void sendHostNotification(Slot slot) {
        log.info("Sending booking confirmation to: {}", slot.getEvent().getHost().getEmail());

        try {
            Map<String, Object> fields = new HashMap<>();
            fields.put("attendeeName", slot.getBookedByName());
            fields.put("attendeeEmail", slot.getBookedByEmail());
            fields.put("eventName", slot.getEvent().getEventName());
            fields.put("startTime", slot.getStartTime());
            fields.put("endTime", slot.getEndTime());
            fields.put("date", slot.getStartTime());
            fields.put("timeZone", slot.getEvent().getTimeZone());

            String htmlContent = renderTemplate("email/booking-notification", fields);

            sendEmail(slot.getEvent().getHost().getEmail(),
                    "New Booking: " + slot.getBookedByName(),
                    htmlContent);

            log.info("Host notification sent successfully for slot {}", slot.getId());
        } catch (Exception e) {
            log.error("Failed to send host notification for slot {}: {}",
                    slot.getId(), e.getMessage(), e);

            // TODO add in retry queue
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

    private String getHostDisplayName(Slot slot) {
        User host = slot.getEvent().getHost();
        String displayName = "";
        if (host.getFirstName() != null) {
            displayName += host.getFirstName();
        }
        if (host.getLastName() != null) {
            displayName += " " + host.getLastName();
        }

        return displayName.trim();
    }
}
