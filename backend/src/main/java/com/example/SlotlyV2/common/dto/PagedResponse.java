package com.example.SlotlyV2.common.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PagedResponse<T> {
    List<T> content;

    @JsonProperty("page")
    PageMetadata metadata;

    public static <T> PagedResponse<T> of(Page<T> page) {
        PageMetadata metadata = PageMetadata.builder()
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();

        return PagedResponse.<T>builder()
                .content(page.getContent())
                .metadata(metadata)
                .build();
    }
}
