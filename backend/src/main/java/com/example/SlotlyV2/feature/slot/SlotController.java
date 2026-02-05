package com.example.SlotlyV2.feature.slot;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.slot.dto.SlotResponse;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SlotController {
    private final SlotService slotService;
    private final UserService userService;
    private final TimeZoneConverter timeZoneConverter;

    @GetMapping("/events/{eventId}/slots")
    public ApiResponse<List<SlotResponse>> getSlots(@PathVariable Long eventId) {
        List<Slot> slots = slotService.getSlots(eventId);
        User currentUser = userService.getCurrentUser();
        String userTimezone = currentUser.getTimeZone();

        List<SlotResponse> slotResponses = slots.stream()
                .map(slot -> new SlotResponse(slot, userTimezone, timeZoneConverter))
                .toList();

        return new ApiResponse<>("Slots fetched successfully", slotResponses);
    }

    @GetMapping("events/{eventId}/slots/{slotId}")
    public ApiResponse<SlotResponse> getSlot(@PathVariable Long eventId, @PathVariable Long slotId) {
        Slot slot = slotService.getSlotById(slotId);
        return new ApiResponse<>("Slot fetched successfully",
                new SlotResponse(slot, userService.getCurrentUser().getTimeZone(), timeZoneConverter));
    }

    @GetMapping("/share/{shareableId}/slots")
    public ApiResponse<List<SlotResponse>> getAvailableSlotsByShareableId(
            @PathVariable String shareableId) {
        List<Slot> availableSlots = slotService.getAvailableSlotsByShareableId(shareableId);

        List<SlotResponse> availableSlotsResponse = availableSlots.stream()
                .map(availableSlot -> new SlotResponse(availableSlot, availableSlot.getEvent().getTimeZone(),
                        timeZoneConverter))
                .toList();

        return new ApiResponse<>("Slots fetched successfully", availableSlotsResponse);
    }
}
