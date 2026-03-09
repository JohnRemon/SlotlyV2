package com.example.SlotlyV2.feature.event;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

import com.example.SlotlyV2.common.dto.DataResponse;
import com.example.SlotlyV2.common.dto.PagedResponse;
import com.example.SlotlyV2.feature.availability.dto.AvailabilityRulesUpdateRequest;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormUpdateRequest;
import com.example.SlotlyV2.feature.event.dto.EventRequest;
import com.example.SlotlyV2.feature.event.dto.EventResponse;
import com.example.SlotlyV2.feature.event.dto.PublicEventResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        return DataResponse.of(eventService.createEvent(request));
    }

    @PostMapping("/recurring")
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse<EventResponse> createRecurringEvent(@Valid @RequestBody EventRequest request) {
        return DataResponse.of(eventService.createRecurringEvent(request));
    }

    @GetMapping
    public PagedResponse<EventResponse> getEvents(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return PagedResponse.of(eventService.getEvents(pageable));
    }

    @GetMapping("/{id}")
    public DataResponse<EventResponse> getEventById(@PathVariable Long id) {
        return DataResponse.of(eventService.getEventById(id));
    }

    @GetMapping("/by-schedule")
    public PagedResponse<EventResponse> getEventsByScheduleId(
            @RequestParam UUID scheduleId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return PagedResponse.of(eventService.getEventsByScheduleId(scheduleId, pageable));
    }

    @GetMapping("/public/{shareableId}")
    public DataResponse<PublicEventResponse> getPublicEvent(@PathVariable String shareableId) {
        return DataResponse.of(eventService.getPublicEvent(shareableId));
    }

    @PutMapping("/{id}")
    public DataResponse<EventResponse> updateEvent(
            @Valid @RequestBody EventRequest request,
            @PathVariable Long id) {
        return DataResponse.of(eventService.updateEvent(request, id));
    }

    @PatchMapping("/{id}/availability-rules")
    public DataResponse<EventResponse> updateAvailabilityRules(
            @Valid @RequestBody AvailabilityRulesUpdateRequest request,
            @PathVariable Long id) {
        return DataResponse.of(eventService.updateAvailabilityRules(request, id));
    }

    @PatchMapping("/{id}/booking-form")
    public DataResponse<EventResponse> updateBookingForm(
            @RequestBody BookingFormUpdateRequest request,
            @PathVariable Long id) {
        return DataResponse.of(eventService.updateBookingForm(request, id));
    }

    @PatchMapping("/{id}/schedule")
    public DataResponse<EventResponse> updateSchedule(
            @PathVariable Long id,
            @RequestParam UUID scheduleId) {
        return DataResponse.of(eventService.updateEventSchedule(scheduleId, id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEventById(id);
    }
}
