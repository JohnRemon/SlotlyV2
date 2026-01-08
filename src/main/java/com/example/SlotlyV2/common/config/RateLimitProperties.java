package com.example.SlotlyV2.common.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.ratelimit")
@Data
public class RateLimitProperties {
    private int globalCapacity = 100;
    private Duration globalRefill = Duration.ofMinutes(1);
    
    private int loginCapacity = 5;
    private Duration loginRefill = Duration.ofMinutes(5);
    
    private int registerCapacity = 3;
    private Duration registerRefill = Duration.ofHours(1);
    
    private int bookingCapacity = 10;
    private Duration bookingRefill = Duration.ofMinutes(1);
    
    private int passwordResetCapacity = 3;
    private Duration passwordResetRefill = Duration.ofHours(1);
}
