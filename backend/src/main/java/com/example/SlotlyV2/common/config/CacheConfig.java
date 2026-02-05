package com.example.SlotlyV2.common.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.github.bucket4j.Bucket;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, Bucket> rateLimitCache() {
        return Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterAccess(Duration.ofHours(2))
                .build();
    }
}
