package com.example.SlotlyV2.feature.schedule;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.DataResponse;
import com.example.SlotlyV2.common.dto.PagedResponse;
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
    public PagedResponse<BlockedPeriodResponse> getBlockedPeriods(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return PagedResponse.of(blockedPeriodService.getBlockedPeriods(pageable));
    }

    @GetMapping
    public DataResponse<BlockedPeriodResponse> getBlockedPeriod(@RequestParam UUID id) {
        return DataResponse.of(blockedPeriodService.getBlockedPeriod(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse<BlockedPeriodResponse> createBlockedPeriod(
            @Valid @RequestBody BlockedPeriodRequest request) {
        return DataResponse.of(blockedPeriodService.createBlockedPeriod(request));
    }

    // TODO: edit blocked period

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBlockedPeriod(@RequestParam UUID id) {
        blockedPeriodService.deleteBlockedPeriod(id);
    }
}
