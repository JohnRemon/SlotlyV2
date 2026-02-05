package com.example.SlotlyV2.feature.booking;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.booking.dto.BookingRequest;
import com.example.SlotlyV2.feature.booking.dto.BookingResponse;
import com.example.SlotlyV2.feature.booking.dto.CancelBookingRequest;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;
    private final TimeZoneConverter timeZoneConverter;

    @PostMapping
    public ApiResponse<BookingResponse> book(@Valid @RequestBody BookingRequest request) {
        Booking booking = bookingService.book(request);
        return new ApiResponse<>("booking created successfully",
                new BookingResponse(booking, booking.getEvent().getTimeZone(), timeZoneConverter));
    }

    @PatchMapping("/{bookingId}")
    public ApiResponse<Void> cancel(@Valid @RequestBody CancelBookingRequest request, @PathVariable Long bookingId) {
        bookingService.cancel(request, bookingId);
        return new ApiResponse<Void>("booking cancelled successfully", null);
    }

    @GetMapping("/me")
    public ApiResponse<List<BookingResponse>> getBookings() {
        User currentUser = userService.getCurrentUser();
        String userTimezone = currentUser.getTimeZone();
        List<Booking> bookings = bookingService.getBookings(currentUser);

        List<BookingResponse> bookingResponses = bookings.stream()
                .map(booking -> new BookingResponse(booking, userTimezone, timeZoneConverter))
                .toList();

        return new ApiResponse<>("Bookings fetched successfully", bookingResponses);

    }
}
