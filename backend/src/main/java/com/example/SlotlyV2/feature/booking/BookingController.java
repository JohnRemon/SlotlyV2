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

import com.example.SlotlyV2.common.dto.DataResponse;
import com.example.SlotlyV2.common.rate_limiting.RateLimitHelper;
import com.example.SlotlyV2.feature.booking.dto.BookingRequest;
import com.example.SlotlyV2.feature.booking.dto.BookingResponse;
import com.example.SlotlyV2.feature.booking.dto.CancelBookingRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final RateLimitHelper rateLimitHelper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse<BookingResponse> book(@Valid @RequestBody BookingRequest request) {
        rateLimitHelper.checkBookingRateLimit(request.getAttendeeEmail());
        return DataResponse.of(bookingService.book(request));
    }

    @GetMapping("/me")
    public DataResponse<List<BookingResponse>> getMyBookings() {
        return DataResponse.of(bookingService.getMyBookings());
    }

    @GetMapping("/{id}")
    public DataResponse<BookingResponse> getBooking(@PathVariable Long id) {
        return DataResponse.of(bookingService.getBooking(id));
    }

    @PatchMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@Valid @RequestBody CancelBookingRequest request, @PathVariable Long id) {
        bookingService.cancel(request, id);
    }

    @PostMapping("/{id}/no-show")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markNoShow(@PathVariable Long id) {
        bookingService.markNoShow(id);
    }
}
