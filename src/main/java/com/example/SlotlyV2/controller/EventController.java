package com.example.SlotlyV2.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.dto.ApiResponse;
import com.example.SlotlyV2.dto.EventRequest;
import com.example.SlotlyV2.dto.EventResponse;
import com.example.SlotlyV2.model.Event;
import com.example.SlotlyV2.service.EventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(@Valid @RequestBody EventRequest request) {
        Event event = eventService.createEvent(request);

        ApiResponse<EventResponse> response = new ApiResponse<>("Event created successfully", new EventResponse(event));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEvents() {
        List<Event> events = eventService.getEvents();

        List<EventResponse> eventResponses = events.stream()
                .map(event -> new EventResponse(event))
                .toList();

        ApiResponse<List<EventResponse>> response = new ApiResponse<>("Events fetched successfully", eventResponses);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);

        ApiResponse<EventResponse> response = new ApiResponse<>("Event fetched successfully", new EventResponse(event));

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEventById(@PathVariable Long id) {
        eventService.deleteEventById(id);

        ApiResponse<Void> response = new ApiResponse<>("Event deleted successfully", null);

        return ResponseEntity.ok().body(response);
    }
}
