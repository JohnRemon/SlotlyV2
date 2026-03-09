package com.example.SlotlyV2.common.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PageMetadata {
    int number;
    int size;
    long totalElements;
    int totalPages;
    boolean first;
    boolean last;
}
