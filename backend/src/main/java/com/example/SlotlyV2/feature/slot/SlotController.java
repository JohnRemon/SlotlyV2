package com.example.SlotlyV2.feature.slot;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.DataResponse;
import com.example.SlotlyV2.feature.slot.dto.SlotResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/slots")
@RequiredArgsConstructor
public class SlotController {
    // TODO: edit the frontend urls
    private final SlotService slotService;

    @GetMapping
    public DataResponse<List<SlotResponse>> getSlots(@RequestParam Long eventId) {
        return DataResponse.of(slotService.getSlots(eventId));
    }

    @GetMapping("/{id}")
    public DataResponse<SlotResponse> getSlotById(@PathVariable Long id) {
        return DataResponse.of(slotService.getSlotById(id));
    }

    @GetMapping("/public")
    public DataResponse<List<SlotResponse>> getAvailableSlots(
            @RequestParam String shareableId,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String timeZone) {
        return DataResponse.of(slotService.getAvailableSlots(shareableId, date, timeZone));

    }
}
