package com.example.SlotlyV2.common.dto;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorResponse {
    String message;
    String path;
    String code;
    int status;
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
