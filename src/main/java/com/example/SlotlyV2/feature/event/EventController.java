package com.example.SlotlyV2.feature.event;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.feature.event.dto.EventRequest;
import com.example.SlotlyV2.feature.event.dto.EventResponse;
import com.example.SlotlyV2.feature.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        Event event = eventService.createEvent(request);
        return new ApiResponse<>("Event created successfully", new EventResponse(event));
    }

    @GetMapping
    public ApiResponse<List<EventResponse>> getEvents() {
        List<EventResponse> events = eventService.getEvents(userService.getCurrentUser());
        return new ApiResponse<>("Events fetched successfully", events);
    }

    @GetMapping("/{id}")
    public ApiResponse<EventResponse> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        return new ApiResponse<>("Event fetched successfully", new EventResponse(event));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEventById(@PathVariable Long id) {
        eventService.deleteEventById(id);
        return new ApiResponse<>("Event deleted successfully", null);
    }

    @GetMapping("/share/{shareableId}")
    public ApiResponse<EventResponse> getEventByShareableId(@PathVariable String shareableId) {
        Event event = eventService.getEventByShareableId(shareableId);
        return new ApiResponse<>("Event fetched successfully", new EventResponse(event));
    }
}
