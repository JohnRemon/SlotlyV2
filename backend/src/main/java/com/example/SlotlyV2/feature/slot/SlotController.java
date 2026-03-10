package com.example.SlotlyV2.feature.slot;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.DataResponse;
import com.example.SlotlyV2.common.dto.PagedResponse;
import com.example.SlotlyV2.feature.slot.dto.SlotResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/slots")
@RequiredArgsConstructor
public class SlotController {
    private final SlotService slotService;

    @GetMapping
    public PagedResponse<SlotResponse> getSlots(@RequestParam Long eventId,
            @PageableDefault(size = 20, page = 0, sort = "startTime") Pageable pageable) {
        return PagedResponse.of(slotService.getSlots(eventId, pageable));
    }

    @GetMapping
    public DataResponse<SlotResponse> getSlotById(@RequestParam Long id) {
        return DataResponse.of(slotService.getSlotById(id));
    }

    @GetMapping("/available")
    public PagedResponse<SlotResponse> getAvailableSlots(
            @RequestParam String shareableId,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String timeZone,
            @PageableDefault(size = 20, page = 0, sort = "startTime") Pageable pageable) {
        return PagedResponse.of(slotService.getAvailableSlots(shareableId, date, timeZone, pageable));

    }
}
