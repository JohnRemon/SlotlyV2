package com.example.SlotlyV2.feature.booking;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.common.rate_limiting.RateLimitHelper;
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
    private final RateLimitHelper rateLimitHelper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BookingResponse> book(@Valid @RequestBody BookingRequest request) {
        rateLimitHelper.checkBookingRateLimit(request.getAttendeeEmail());
        Booking booking = bookingService.book(request);
        return new ApiResponse<>("booking created successfully",
                new BookingResponse(booking, booking.getEvent().getTimeZone(), timeZoneConverter));
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@Valid @RequestBody CancelBookingRequest request, @PathVariable Long id) {
        bookingService.cancel(request, id);
        return new ApiResponse<Void>("booking cancelled successfully", null);
    }

    @PostMapping("{id}/no-show")
    public ApiResponse<Void> markNoShow(@PathVariable Long id) {
        bookingService.markNoShow(id);
        return new ApiResponse<>("booking marked as no show successfully", null);
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

    @GetMapping
    public ApiResponse<BookingResponse> getBooking(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        String userTimezone = currentUser.getTimeZone();
        Booking booking = bookingService.getBooking(id);
        return new ApiResponse<>("Booking fetched successfully",
                new BookingResponse(booking, userTimezone, timeZoneConverter));
    }
}
