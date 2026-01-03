package com.example.SlotlyV2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import com.example.SlotlyV2.config.RateLimitProperties;

@ExtendWith(MockitoExtension.class)
public class RateLimiterServiceTest {

    @Mock
    private RateLimitProperties rateLimitProperties;

    @InjectMocks
    private RateLimitService rateLimitService;

    @Test
    void shouldGetIpFromXForwardedForHeader() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "1.1.1.1, 2.2.2.2");

        // Act
        String clientIp = rateLimitService.getClientIp(request);

        // Assert
        assertEquals("1.1.1.1", clientIp);
    }

    @Test
    void shouldGetRemoteAddrWhenNoHeader() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("1.1.1.1");

        // Act
        String clientIp = rateLimitService.getClientIp(request);

        // Assert
        assertEquals("1.1.1.1", clientIp);
    }
}
