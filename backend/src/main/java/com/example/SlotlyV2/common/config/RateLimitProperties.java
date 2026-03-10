package com.example.SlotlyV2.common.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.ratelimit")
@Data
public class RateLimitProperties {
    private int globalCapacity;
    private Duration globalRefill;
    private int loginCapacity;
    private Duration loginRefill;
    private int registerCapacity;
    private Duration registerRefill;
    private int bookingCapacity;
    private Duration bookingRefill;
    private int passwordResetCapacity;
    private Duration passwordResetRefill;
}
