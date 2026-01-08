package com.example.SlotlyV2.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "email")
public class EmailConfig {
    private String fromEmail;
    private String fromName;
}
