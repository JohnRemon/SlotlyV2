package com.example.SlotlyV2.util;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.exception.RateLimitExceededException;
import com.example.SlotlyV2.service.RateLimitService;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitHelper {
    private final RateLimitService rateLimitService;

    public void checkLoginRateLimit(HttpServletRequest request) {
        String ip = rateLimitService.getClientIp(request);
        Bucket bucket = rateLimitService.getLoginBucket(ip);

        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException(
                    "Too many login attempts. Please try again in a few minutes",
                    300);
        }
    }

    public void checkRegisterRateLimit(HttpServletRequest request) {
        String ip = rateLimitService.getClientIp(request);
        Bucket bucket = rateLimitService.getRegisterBucket(ip);

        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException(
                    "Too many registration attempts. Please try again later",
                    3600);
        }
    }

    public void checkBookingRateLimit(String email) {
        Bucket bucket = rateLimitService.getBookingBucket(email);

        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException(
                    "You can only book 10 slots per minute. Please try again later",
                    60);
        }
    }

    public void checkPasswordResetRateLimit(String email) {
        Bucket bucket = rateLimitService.getPasswordResetBucket(email);

        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException(
                    "Too many password reset requests. Please try again later",
                    3600);
        }
    }


}
