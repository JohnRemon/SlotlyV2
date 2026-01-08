package com.example.SlotlyV2.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import com.example.SlotlyV2.common.exception.auth.RateLimitExceededException;
import com.example.SlotlyV2.common.rate_limiting.RateLimitService;
import com.example.SlotlyV2.common.util.RateLimitHelper;

import io.github.bucket4j.Bucket;

@ExtendWith(MockitoExtension.class)
public class RateLimitHelperTest {

    @Mock
    private RateLimitService rateLimitService;

    @InjectMocks
    private RateLimitHelper rateLimitHelper;

    @Test
    void shouldBlockLoginRequestAfter5Attempts() {
        // Arrange
        Bucket mockBucket = mock(Bucket.class);
        when(rateLimitService.getClientIp(any())).thenReturn("1.1.1.1");
        when(rateLimitService.getLoginBucket(anyString())).thenReturn(mockBucket);
        when(mockBucket.tryConsume(1))
                .thenReturn(true, true, true, true, true)
                .thenReturn(false);

        MockHttpServletRequest request = new MockHttpServletRequest();

        // Act
        for (int i = 0; i < 5; i++) {
            rateLimitHelper.checkLoginRateLimit(request);
        }

        // Assert
        assertThrows(RateLimitExceededException.class, () -> rateLimitHelper.checkLoginRateLimit(request));
    }

    @Test
    void shouldBlockRegisterRequestAfter3Attempts() {
        // Arrange
        Bucket mockBucket = mock(Bucket.class);
        when(rateLimitService.getClientIp(any())).thenReturn("1.1.1.1");
        when(rateLimitService.getRegisterBucket(anyString())).thenReturn(mockBucket);
        when(mockBucket.tryConsume(1))
                .thenReturn(true, true, true)
                .thenReturn(false);

        MockHttpServletRequest request = new MockHttpServletRequest();

        // Act
        for (int i = 0; i < 3; i++) {
            rateLimitHelper.checkRegisterRateLimit(request);
        }

        // Assert
        assertThrows(RateLimitExceededException.class, () -> rateLimitHelper.checkRegisterRateLimit(request));
    }

    @Test
    void shouldBlockBookingRequestAfter10Attempts() {
        // Arrange
        Bucket mockBucket = mock(Bucket.class);
        when(rateLimitService.getBookingBucket(anyString())).thenReturn(mockBucket);
        when(mockBucket.tryConsume(1))
                .thenReturn(true, true, true, true, true, true, true, true, true, true)
                .thenReturn(false);

        // Act
        for (int i = 0; i < 10; i++) {
            rateLimitHelper.checkBookingRateLimit("test@example.com");
        }

        // Assert
        assertThrows(RateLimitExceededException.class, () -> rateLimitHelper.checkBookingRateLimit("test@example.com"));
    }

    @Test
    void shouldBlockPasswordResetRequestAfter3Attempts() {
        // Arrange
        Bucket mockBucket = mock(Bucket.class);
        when(rateLimitService.getPasswordResetBucket(anyString())).thenReturn(mockBucket);
        when(mockBucket.tryConsume(1))
                .thenReturn(true, true, true)
                .thenReturn(false);

        // Act
        for (int i = 0; i < 3; i++) {
            rateLimitHelper.checkPasswordResetRateLimit("test@example.com");
        }

        // Assert
        assertThrows(RateLimitExceededException.class,
                () -> rateLimitHelper.checkPasswordResetRateLimit("test@example.com"));
    }
}
