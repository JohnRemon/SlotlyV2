package com.example.SlotlyV2.common.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DataResponse<T> {
    T data;

    public static <T> DataResponse<T> of(T data) {
        return DataResponse.<T>builder().data(data).build();
    }
}
