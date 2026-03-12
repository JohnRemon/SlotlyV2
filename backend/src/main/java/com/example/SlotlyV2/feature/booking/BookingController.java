package com.example.SlotlyV2.feature.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping
    public PagedResponse<BookingResponse> getBookings(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam String timeZone) {
        return PagedResponse.of(bookingService.getBookings(pageable, timeZone));
    }

    @GetMapping("/{id}")
    public DataResponse<BookingResponse> getBookingById(
            @PathVariable Long id,
            @RequestParam String timeZone) {
        return DataResponse.of(bookingService.getBooking(id, timeZone));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            @RequestParam String timeZone) {
        rateLimitHelper.checkBookingRateLimit(request.getAttendeeEmail());
        return DataResponse.of(bookingService.book(request, timeZone));
    }

    @PostMapping("/{id}/no-show")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markBookingNoShow(@PathVariable Long id) {
        bookingService.markBookingNoShow(id);
    }

    @PatchMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelBooking(
            @PathVariable Long id,
            @Valid @RequestBody CancelBookingRequest request) {
        bookingService.cancelBooking(id, request);
    }

}
