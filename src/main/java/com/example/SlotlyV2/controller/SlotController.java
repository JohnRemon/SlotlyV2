package com.example.SlotlyV2.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("api/")
public class SlotController {
    private final SlotService slotService;
    private final UserService userService;

    public SlotController(SlotService slotService, UserService userService) {
        this.slotService = slotService;
        this.userService = userService;
    }

    @GetMapping("events/{eventId}/slots")
    public ResponseEntity<ApiResponse<List<SlotResponse>>> getSlots(@PathVariable Long eventId) {
        List<Slot> slots = slotService.getSlots(eventId);

        List<SlotResponse> slotResponses = slots.stream()
                .map(slot -> new SlotResponse(slot))
                .toList();

        ApiResponse<List<SlotResponse>> response = new ApiResponse<>("Slots fetched successfully", slotResponses);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("{shareableId}")
    public ResponseEntity<ApiResponse<SlotResponse>> bookSlot(@RequestBody SlotRequest request) {
        Slot bookedSlot = slotService.bookSlot(request);

        ApiResponse<SlotResponse> response = new ApiResponse<>("Slot booked successfully",
                new SlotResponse(bookedSlot));

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("users/me/bookings")
    public ResponseEntity<ApiResponse<List<SlotResponse>>> getBookedSlots() {
        User currentUser = userService.getCurrentUser();
        List<Slot> slots = slotService.getBookedSlots(currentUser);

        List<SlotResponse> slotResponses = slots.stream()
                .map(slot -> new SlotResponse(slot))
                .toList();

        ApiResponse<List<SlotResponse>> response = new ApiResponse<>("Booked Slots fetched successfully",
                slotResponses);

        return ResponseEntity.ok().body(response);

    }
}
