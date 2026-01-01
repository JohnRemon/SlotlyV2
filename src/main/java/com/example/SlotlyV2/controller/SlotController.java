package com.example.SlotlyV2.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.dto.ApiResponse;
import com.example.SlotlyV2.dto.SlotRequest;
import com.example.SlotlyV2.dto.SlotResponse;
import com.example.SlotlyV2.model.Slot;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.service.SlotService;
import com.example.SlotlyV2.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/")
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

    @PostMapping("{shareableId}")
    public ApiResponse<SlotResponse> bookSlot(@RequestBody @Valid SlotRequest request) {
        Slot bookedSlot = slotService.bookSlot(request);
        return new ApiResponse<>("Slot booked successfully", new SlotResponse(bookedSlot));
    }

    @GetMapping("{shareableId}")
    public ApiResponse<List<SlotResponse>> getAvailableSlotsByShareableId(
            @PathVariable String shareableId) {
        List<Slot> availableSlots = slotService.getAvailableSlotsByShareableId(shareableId);

        List<SlotResponse> availableSlotsRespones = availableSlots.stream()
                .map(availableSlot -> new SlotResponse(availableSlot))
                .toList();

        return new ApiResponse<>("Slots fetched successfully", availableSlotsRespones);
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
