package com.example.SlotlyV2.feature.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.DataResponse;
import com.example.SlotlyV2.common.dto.PagedResponse;
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
    public PagedResponse<BookingResponse> getMyBookings(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        return PagedResponse.of(bookingService.getMyBookings(pageable));
    }

    @GetMapping
    public DataResponse<BookingResponse> getBooking(@RequestParam Long id) {
        return DataResponse.of(bookingService.getBooking(id));
    }

    @PatchMapping("/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@Valid @RequestBody CancelBookingRequest request) {
        bookingService.cancel(request);
    }

    @PostMapping("/no-show")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markNoShow(@RequestParam Long id) {
        bookingService.markNoShow(id);
    }
}
