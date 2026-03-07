package com.example.SlotlyV2.feature.slot;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.slot.dto.SlotResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class SlotController {
    private final SlotService slotService;
    private final TimeZoneConverter timeZoneConverter;

    @GetMapping("/{eventId}/slots")
    public ApiResponse<List<SlotResponse>> getSlots(@PathVariable Long eventId) {
        List<Slot> slots = slotService.getSlots(eventId);

        List<SlotResponse> slotResponses = slots.stream()
                .map(slot -> new SlotResponse(slot, timeZoneConverter))
                .toList();

        return new ApiResponse<>("Slots fetched successfully", slotResponses);
    }

    @GetMapping("/{eventId}/slots/{slotId}")
    public ApiResponse<SlotResponse> getSlot(@PathVariable Long eventId, @PathVariable Long slotId) {
        Slot slot = slotService.getSlotById(slotId);
        return new ApiResponse<>("Slot fetched successfully", new SlotResponse(slot, timeZoneConverter));
    }

    @GetMapping("/public/{shareableId}/slots")
    public ApiResponse<List<SlotResponse>> getAvailableSlotsByShareableId(
            @PathVariable String shareableId,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String timeZone) {
        List<Slot> availableSlots;

        if (date == null && timeZone == null) {
            availableSlots = slotService.getAvailableSlotsByShareableId(shareableId);
        } else {
            availableSlots = slotService.getAvailableSlotsByShareableIdAndDate(shareableId, date, timeZone);
        }

        List<SlotResponse> availableSlotsResponse = availableSlots.stream()
                .map(availableSlot -> new SlotResponse(availableSlot, timeZoneConverter))
                .toList();

        return new ApiResponse<>("Slots fetched successfully", availableSlotsResponse);
    }

}
