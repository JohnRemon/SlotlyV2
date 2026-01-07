package com.example.SlotlyV2.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.dto.ApiResponse;
import com.example.SlotlyV2.dto.CancelBookingRequest;
import com.example.SlotlyV2.dto.SlotResponse;
import com.example.SlotlyV2.model.Slot;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.service.SlotService;
import com.example.SlotlyV2.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SlotController {
    private final SlotService slotService;
    private final UserService userService;

    @GetMapping("events/{eventId}/slots")
    public ApiResponse<List<SlotResponse>> getSlots(@PathVariable Long eventId) {
        List<Slot> slots = slotService.getSlots(eventId);

        List<SlotResponse> slotResponses = slots.stream()
                .map(slot -> new SlotResponse(slot))
                .toList();

        return new ApiResponse<>("Slots fetched successfully", slotResponses);
    }

    @PostMapping("slots/cancel")
    public ApiResponse<SlotResponse> cancelBooking(@Valid @RequestBody CancelBookingRequest request) {
        Slot cancelledSlot = slotService.cancelBooking(request);
        return new ApiResponse<>("Slot booking cancelled successfully", new SlotResponse(cancelledSlot));
    }

    @GetMapping("share/{shareableId}/slots")
    public ApiResponse<List<SlotResponse>> getAvailableSlotsByShareableId(
            @PathVariable String shareableId) {
        List<Slot> availableSlots = slotService.getAvailableSlotsByShareableId(shareableId);

        List<SlotResponse> availableSlotsResponse = availableSlots.stream()
                .map(availableSlot -> new SlotResponse(availableSlot))
                .toList();

        return new ApiResponse<>("Slots fetched successfully", availableSlotsResponse);
    }

    @GetMapping("users/me/bookings")
    public ApiResponse<List<SlotResponse>> getBookedSlots() {
        User currentUser = userService.getCurrentUser();
        List<Slot> slots = slotService.getBookedSlots(currentUser);

        List<SlotResponse> slotResponses = slots.stream()
                .map(slot -> new SlotResponse(slot))
                .toList();

        return new ApiResponse<>("Booked Slots fetched successfully", slotResponses);
    }
}
