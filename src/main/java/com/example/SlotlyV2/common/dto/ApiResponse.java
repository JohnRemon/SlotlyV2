package com.example.SlotlyV2.common.dto;

import lombok.Value;

@Value
public class ApiResponse<T> {
    private String message;
    private T data;
}
