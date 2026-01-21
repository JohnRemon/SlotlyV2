package com.example.SlotlyV2.feature.calendar;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.feature.calendar.dto.GoogleEventRequest;
import com.google.api.services.calendar.model.Event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/calendar/google")
@RequiredArgsConstructor
public class GoogleCalendarController {

    private final GoogleCalendarService calendarService;

    @GetMapping("/events")
    public ApiResponse<List<Event>> getUpcomingEvents(
            @RequestParam(defaultValue = "100") int maxResults) {
        try {
            List<Event> events = calendarService.getUpcomingEvents(maxResults);
            return new ApiResponse<>("Fetched google calendar events successfully", events);
        } catch (IOException e) {
            return new ApiResponse<>("Error fetching google calendar evnets: " + e.getMessage(), null);
        }
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Event> createEvent(@RequestBody @Valid GoogleEventRequest request) {
        try {
            Event event = calendarService.createEvent(
                    request.getSummary(),
                    request.getDescription(),
                    request.getStartTime(),
                    request.getEndtime(),
                    request.getTimeZone());

            return new ApiResponse<>("Event created successfully", event);
        } catch (IOException e) {
            return new ApiResponse<>("Error creating google calendar event: " + e.getMessage(), null);
        }
    }

    @PutMapping("/events/{evendId}")
    public ApiResponse<Event> updateEvent(@PathVariable String eventId,
            @RequestBody @Valid GoogleEventRequest request) {
        try {
            Event event = calendarService.updateEvent(
                    eventId,
                    request.getSummary(),
                    request.getDescription(),
                    request.getStartTime(),
                    request.getEndtime(),
                    request.getTimeZone());

            return new ApiResponse<>("Event updated successfully", event);
        } catch (Exception e) {
            return new ApiResponse<>("Error updating google calendar event: " + e.getMessage(), null);
        }
    }

    @DeleteMapping("/events/{eventId}")
    public ApiResponse<Void> deleteEvent(@PathVariable String eventId) {
        try {
            calendarService.deleteEvent(eventId);
            return new ApiResponse<>("Event deleted successfully", null);
        } catch (Exception e) {
            return new ApiResponse<>("Error deleting google calendar event: " + e.getMessage(), null);
        }
    }
}
