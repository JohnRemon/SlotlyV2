package com.example.SlotlyV2.feature.slot;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.slot.dto.CancelBookingRequest;
import com.example.SlotlyV2.feature.slot.dto.SlotRequest;
import com.example.SlotlyV2.feature.slot.dto.SlotResponse;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserService;

import jakarta.validation.Valid;
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

    @PostMapping("/slots/book")
    public ApiResponse<SlotResponse> bookSlot(@Valid @RequestBody SlotRequest request) {
        Slot bookedSlot = slotService.bookSlot(request);
        User currentUser = userService.getCurrentUser();
        return new ApiResponse<>("Slot booked successfully",
                new SlotResponse(bookedSlot, currentUser.getTimeZone(), timeZoneConverter));
    }

    @PostMapping("/slots/cancel")
    public ApiResponse<SlotResponse> cancelBooking(@Valid @RequestBody CancelBookingRequest request) {
        Slot cancelledSlot = slotService.cancelBooking(request);
        User currentUser = userService.getCurrentUser();
        return new ApiResponse<>("Slot booking cancelled successfully",
                new SlotResponse(cancelledSlot, currentUser.getTimeZone(), timeZoneConverter));
    }

    @GetMapping("/share/{shareableId}/slots")
    public ApiResponse<List<SlotResponse>> getAvailableSlotsByShareableId(
            @PathVariable String shareableId) {
        List<Slot> availableSlots = slotService.getAvailableSlotsByShareableId(shareableId);

        List<SlotResponse> availableSlotsResponse = availableSlots.stream()
                .map(availableSlot -> new SlotResponse(availableSlot, availableSlot.getEvent().getTimeZone(), timeZoneConverter))
                .toList();

        return new ApiResponse<>("Slots fetched successfully", availableSlotsResponse);
    }

    @GetMapping("/users/me/bookings")
    public ApiResponse<List<SlotResponse>> getBookedSlots() {
        User currentUser = userService.getCurrentUser();
        List<Slot> slots = slotService.getBookedSlots(currentUser);
        String userTimezone = currentUser.getTimeZone();

        List<SlotResponse> slotResponses = slots.stream()
                .map(slot -> new SlotResponse(slot, userTimezone, timeZoneConverter))
                .toList();

        return new ApiResponse<>("Booked Slots fetched successfully", slotResponses);
    }
}
