package com.example.SlotlyV2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import com.example.SlotlyV2.common.config.RateLimitProperties;
import com.example.SlotlyV2.common.rate_limiting.RateLimitService;
import com.github.benmanes.caffeine.cache.Cache;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

@ExtendWith(MockitoExtension.class)
public class RateLimiterServiceTest {

    @Mock
    private RateLimitProperties rateLimitProperties;

    @Mock
    private Cache<String, Bucket> cache;

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

    @Test
    void shouldResolveBucketSuccessfully() {
        // Arrange
        String key = "test-key";
        Bandwidth limit = Bandwidth.builder()
                .capacity(10)
                .refillGreedy(10, Duration.ofMinutes(1))
                .build();

        // Don't create a real bucket - mock it instead
        Bucket mockBucket = mock(Bucket.class);
        when(cache.get(eq(key), any())).thenReturn(mockBucket);

        // Act
        Bucket result = rateLimitService.resolveBucket(key, limit);

        // Assert
        assertNotNull(result);
        assertSame(mockBucket, result);
        verify(cache).get(eq(key), any());
    }

    @Test
    void shouldCreateNewBucketWhenNotInCache() {
        // Arrange
        String key = "test-key";
        Bandwidth limit = Bandwidth.builder()
                .capacity(10)
                .refillGreedy(10, Duration.ofMinutes(1))
                .build();

        when(cache.get(eq(key), any())).thenAnswer(invocation -> {
            return invocation.getArgument(1, Function.class).apply(key);
        });

        // Act
        Bucket result = rateLimitService.resolveBucket(key, limit);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getAvailableTokens());
        verify(cache).get(eq(key), any());
    }

    @Test
    void shouldGetGlobalBucket() {
        // Arrange
        String ip = "1.1.1.1";
        String key = "global:" + ip;

        when(rateLimitProperties.getGlobalCapacity()).thenReturn(100);
        when(rateLimitProperties.getGlobalRefill()).thenReturn(Duration.ofMinutes(1));

        Bucket mockBucket = mock(Bucket.class);
        when(cache.get(eq(key), any())).thenReturn(mockBucket);

        // Act
        Bucket result = rateLimitService.getGlobalBucket(ip);

        // Assert
        assertNotNull(result);
        verify(cache).get(eq(key), any());
    }

    @Test
    void shouldGetLoginBucket() {
        // Arrange
        String ip = "1.1.1.1";
        String key = "login:" + ip;

        when(rateLimitProperties.getLoginCapacity()).thenReturn(5);
        when(rateLimitProperties.getLoginRefill()).thenReturn(Duration.ofMinutes(5));

        Bucket mockBucket = mock(Bucket.class);
        when(cache.get(eq(key), any())).thenReturn(mockBucket);

        // Act
        Bucket result = rateLimitService.getLoginBucket(ip);

        // Assert
        assertNotNull(result);
        verify(cache).get(eq(key), any());
    }

    @Test
    void shouldGetRegisterBucket() {
        // Arrange
        String ip = "1.1.1.1";
        String key = "register:" + ip;

        when(rateLimitProperties.getRegisterCapacity()).thenReturn(3);
        when(rateLimitProperties.getRegisterRefill()).thenReturn(Duration.ofHours(1));

        Bucket mockBucket = mock(Bucket.class);
        when(cache.get(eq(key), any())).thenReturn(mockBucket);

        // Act
        Bucket result = rateLimitService.getRegisterBucket(ip);

        // Assert
        assertNotNull(result);
        verify(cache).get(eq(key), any());
    }

    @Test
    void shouldGetBookingBucket() {
        // Arrange
        String email = "test@example.com";
        String key = "email:" + email;

        when(rateLimitProperties.getBookingCapacity()).thenReturn(10);
        when(rateLimitProperties.getBookingRefill()).thenReturn(Duration.ofMinutes(1));

        Bucket mockBucket = mock(Bucket.class);
        when(cache.get(eq(key), any())).thenReturn(mockBucket);

        // Act
        Bucket result = rateLimitService.getBookingBucket(email);

        // Assert
        assertNotNull(result);
        verify(cache).get(eq(key), any());
    }

    @Test
    void shouldGetPasswordResetBucket() {
        // Arrange
        String email = "test@example.com";
        String key = "password-reset:" + email;

        when(rateLimitProperties.getPasswordResetCapacity()).thenReturn(3);
        when(rateLimitProperties.getPasswordResetRefill()).thenReturn(Duration.ofHours(1));

        Bucket mockBucket = mock(Bucket.class);
        when(cache.get(eq(key), any())).thenReturn(mockBucket);

        // Act
        Bucket result = rateLimitService.getPasswordResetBucket(email);

        // Assert
        assertNotNull(result);
        verify(cache).get(eq(key), any());
    }
}
