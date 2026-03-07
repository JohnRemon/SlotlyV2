package com.example.SlotlyV2.feature.event;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.common.dto.PagedResponse;
import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.availability.dto.AvailabilityRulesUpdateRequest;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormUpdateRequest;
import com.example.SlotlyV2.feature.event.dto.EventRequest;
import com.example.SlotlyV2.feature.event.dto.EventResponse;
import com.example.SlotlyV2.feature.event.dto.PublicEventResponse;
import com.example.SlotlyV2.feature.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final UserService userService;
    private final TimeZoneConverter timeZoneConverter;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        Event event = eventService.createEvent(request);
        return new ApiResponse<>("Event created successfully", new EventResponse(event, timeZoneConverter));
    }

    @GetMapping("/{id}")
    public ApiResponse<EventResponse> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        return new ApiResponse<>("Event fetched successfully", new EventResponse(event, timeZoneConverter));
    }

    @GetMapping
    public ApiResponse<PagedResponse<EventResponse>> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<EventResponse> events = eventService.getEvents(userService.getCurrentUser(), pageable);
        return new ApiResponse<>("Events fetched successfully",
                PagedResponse.of(events));
    }

    @PutMapping("/{id}")
    public ApiResponse<EventResponse> updateEvent(@Valid @RequestBody EventRequest request, @PathVariable Long id) {
        Event event = eventService.updateEvent(request, id);
        return new ApiResponse<EventResponse>("Event edited successfully", new EventResponse(event, timeZoneConverter));
    }

    @PatchMapping("/{id}/availability-rules")
    public ApiResponse<EventResponse> updateAvailabilityRules(@RequestBody AvailabilityRulesUpdateRequest request,
            @PathVariable Long id) {
        Event event = eventService.updateAvailabilityRules(request, id);
        return new ApiResponse<>("Availability rules updated successfully",
                new EventResponse(event, timeZoneConverter));
    }

    @PatchMapping("/{id}/booking-form")
    public ApiResponse<EventResponse> updateBookingForm(@RequestBody BookingFormUpdateRequest request,
            @PathVariable Long id) {
        Event event = eventService.updateBookingForm(request, id);
        return new ApiResponse<>("Booking form updated successfully", new EventResponse(event, timeZoneConverter));
    }

    @PatchMapping("/{id}/schedule")
    public ApiResponse<EventResponse> updateSchedule(@PathVariable Long id, @RequestParam UUID scheduleId) {
        Event event = eventService.updateEventSchedule(scheduleId, id);
        return new ApiResponse<>("Schedule updated successfully", new EventResponse(event, timeZoneConverter));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEventById(@PathVariable Long id) {
        eventService.deleteEventById(id);
        return new ApiResponse<>("Event deleted successfully", null);
    }

    @PostMapping("/recurring")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EventResponse> createRecurringEvent(@Valid @RequestBody EventRequest request) {
        Event event = eventService.createRecurringEvent(request);
        return new ApiResponse<>("Event created successfully", new EventResponse(event, timeZoneConverter));
    }

    @GetMapping("/public/{shareableId}")
    public ApiResponse<PublicEventResponse> getEventByShareableId(@PathVariable String shareableId) {
        Event event = eventService.getEventByShareableId(shareableId);
        return new ApiResponse<>("Event fetched successfully", new PublicEventResponse(event, timeZoneConverter));
    }
}
