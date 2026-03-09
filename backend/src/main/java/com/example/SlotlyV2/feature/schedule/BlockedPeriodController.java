package com.example.SlotlyV2.feature.schedule;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.DataResponse;
import com.example.SlotlyV2.feature.schedule.dto.BlockedPeriodRequest;
import com.example.SlotlyV2.feature.schedule.dto.BlockedPeriodResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/blocked-periods")
@RequiredArgsConstructor
public class BlockedPeriodController {
    private final BlockedPeriodService blockedPeriodService;

    @GetMapping
    public DataResponse<List<BlockedPeriodResponse>> getBlockedPeriods() {
        return DataResponse.of(blockedPeriodService.getBlockedPeriods());
    }

    @GetMapping("/{id}")
    public DataResponse<BlockedPeriodResponse> getBlockedPeriodById(@PathVariable UUID id) {
        return DataResponse.of(blockedPeriodService.getBlockedPeriod(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse<BlockedPeriodResponse> createBlockedPeriod(
            @Valid @RequestBody BlockedPeriodRequest request) {
        return DataResponse.of(blockedPeriodService.createBlockedPeriod(request));
    }
}
