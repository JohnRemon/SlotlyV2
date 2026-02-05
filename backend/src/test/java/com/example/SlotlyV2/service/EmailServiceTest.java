package com.example.SlotlyV2.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.example.SlotlyV2.common.config.EmailConfig;
import com.example.SlotlyV2.feature.email.EmailService;
import com.resend.Resend;
import com.resend.services.emails.Emails;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private EmailConfig emailConfig;

    @Mock
    private Resend resend;

    @Mock
    private Emails emails;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        when(resend.emails()).thenReturn(emails);
    }

    @Test
    void shouldSendEmailSuccessfully() throws Exception {
        // Act
        emailService.sendEmail(
                "test@example.com",
                "Test",
                "<h1>Test Email</h1>");

        verify(emails).send(any());
    }

}
