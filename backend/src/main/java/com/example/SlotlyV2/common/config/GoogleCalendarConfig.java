package com.example.SlotlyV2.common.config;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import lombok.Data;

@Configuration
@Data
@ConfigurationProperties(prefix = "google.calendar")
public class GoogleCalendarConfig {
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private String applicationName;
    private String clientId;
    private String clientSecret;
    private String redirectUri;

    @Bean
    public JsonFactory jsonFactory() {
        return JSON_FACTORY;
    }

    @Bean
    public NetHttpTransport netHttpTransport() throws GeneralSecurityException, IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }
}
