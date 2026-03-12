package com.example.SlotlyV2.feature.slot;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public PagedResponse<SlotResponse> getSlots(
            @RequestParam Long eventId,
            @RequestParam String timeZone,
            @PageableDefault(size = 20, sort = "startTime") Pageable pageable) {
        return PagedResponse.of(slotService.getSlots(eventId, pageable, timeZone));
    }

    @GetMapping("/{id}")
    public DataResponse<SlotResponse> getSlotById(@PathVariable Long id, String timeZone) {
        return DataResponse.of(slotService.getSlotById(id, timeZone));
    }

    @GetMapping("/available")
    public PagedResponse<SlotResponse> getAvailableSlots(
            @RequestParam String shareableId,
            @RequestParam String timeZone,
            @RequestParam LocalDate date,
            @PageableDefault(size = 20, sort = "startTime") Pageable pageable) {
        return PagedResponse.of(slotService.getAvailableSlots(shareableId, date, timeZone, pageable));
    }
}
