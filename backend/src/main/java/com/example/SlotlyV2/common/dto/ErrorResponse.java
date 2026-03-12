package com.example.SlotlyV2.common.dto;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorResponse {
    @JsonProperty(index = 0)
    String code;

    @JsonProperty(index = 5)
    String message;

    @JsonProperty(index = 10)
    String path;

    @JsonProperty(index = 15)
    int status;

    @JsonProperty(index = 20)
    Map<String, String> details;

    public static ErrorResponse of(String message, String path, String code, HttpStatus status) {
        return ErrorResponse.builder()
                .message(message)
                .path(path)
                .code(code)
                .status(status.value())
                .build();
    }

    public static ErrorResponse of(String message, String path, String code,
            HttpStatus status, Map<String, String> details) {
        return ErrorResponse.builder()
                .message(message)
                .path(path)
                .code(code)
                .status(status.value())
                .details(details)
                .build();
    }
}
