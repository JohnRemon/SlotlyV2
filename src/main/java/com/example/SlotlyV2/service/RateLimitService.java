package com.example.SlotlyV2.service;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.example.SlotlyV2.config.RateLimitProperties;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class RateLimitService {
    private final RateLimitProperties rateLimitProperties;
    private final Cache<String, Bucket> cache;

    public RateLimitService(RateLimitProperties rateLimitProperties) {
        this.rateLimitProperties = rateLimitProperties;
        this.cache = Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterAccess(Duration.ofHours(2))
                .build();
    }

    public Bucket resolveBucket(String key, Bandwidth limit) {
        return cache.get(key, k -> Bucket.builder()
                .addLimit(limit)
                .build());
    }

    public Bucket getGlobalBucket(String ip) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(rateLimitProperties.getGlobalCapacity())
                .refillGreedy(rateLimitProperties.getGlobalCapacity(), rateLimitProperties.getGlobalRefill())
                .build();

        return resolveBucket("global:" + ip, limit);
    }

    public Bucket getLoginBucket(String ip) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(rateLimitProperties.getLoginCapacity())
                .refillGreedy(rateLimitProperties.getLoginCapacity(), rateLimitProperties.getLoginRefill())
                .build();

        return resolveBucket("login:" + ip, limit);

    }

    public Bucket getRegisterBucket(String ip) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(rateLimitProperties.getRegisterCapacity())
                .refillGreedy(rateLimitProperties.getRegisterCapacity(), rateLimitProperties.getRegisterRefill())
                .build();

        return resolveBucket("register:" + ip, limit);
    }

    public Bucket getBookingBucket(String email) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(rateLimitProperties.getBookingCapacity())
                .refillGreedy(rateLimitProperties.getBookingCapacity(), rateLimitProperties.getBookingRefill())
                .build();

        return resolveBucket("email:" + email, limit);
    }

    public Bucket getPasswordResetBucket(String email) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(rateLimitProperties.getPasswordResetCapacity())
                .refillGreedy(rateLimitProperties.getPasswordResetCapacity(),
                        rateLimitProperties.getPasswordResetRefill())
                .build();

        return resolveBucket("password-reset:" + email, limit);
    }

    public String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
