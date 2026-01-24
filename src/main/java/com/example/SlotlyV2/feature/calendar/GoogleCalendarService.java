package com.example.SlotlyV2.feature.calendar;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.exception.calendar.GoogleCalendarException;
import com.example.SlotlyV2.feature.calendar.dto.GoogleEventRequest;
import com.example.SlotlyV2.feature.user.User;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarService {

    private final GoogleOAuth2Service oAuth2Service;
    private final HttpTransport netHttpTransport;
    private final JsonFactory jsonFactory;

    private static final String PRIMARY_CALENDAR = "primary";

    public Event createEvent(User user, GoogleEventRequest request) {
        try {
            Calendar service = getCalendarService(user);
            Event event = buildEvent(request);

            Event createdEvent = service.events()
                    .insert(PRIMARY_CALENDAR, event)
                    .execute();

            log.info("Created Google Calendar event {} for user {}",
                    createdEvent.getId(), user.getId());

            return createdEvent;

        } catch (IOException e) {
            log.error("Failed to create event for user {}", user.getId(), e);
            throw new GoogleCalendarException("Failed to create calendar event");
        }
    }

    public void updateEvent(User user, String googleEventId, GoogleEventRequest request) {
        try {
            Calendar service = getCalendarService(user);

            Event event = service.events()
                    .get(PRIMARY_CALENDAR, googleEventId)
                    .execute();

            updateEventDetails(event, request);

            service.events()
                    .update(PRIMARY_CALENDAR, googleEventId, event)
                    .execute();

            log.info("Updated Google Calendar event {} for user {}",
                    googleEventId, user.getId());

        } catch (IOException e) {
            log.error("Failed to update event {} for user {}",
                    googleEventId, user.getId(), e);
            throw new GoogleCalendarException("Failed to update calendar event");
        }
    }

    public void deleteEvent(User user, String googleEventId) {
        try {
            Calendar service = getCalendarService(user);

            service.events()
                    .delete(PRIMARY_CALENDAR, googleEventId)
                    .execute();

            log.info("Deleted Google Calendar event {} for user {}",
                    googleEventId, user.getId());

        } catch (IOException e) {
            log.error("Failed to delete event {} for user {}",
                    googleEventId, user.getId(), e);
            throw new GoogleCalendarException("Failed to delete calendar event");
        }
    }

    public List<Event> getUpcomingEvents(User user, int maxResults) {
        try {
            Calendar service = getCalendarService(user);
            DateTime now = new DateTime(System.currentTimeMillis());

            Events events = service.events()
                    .list(PRIMARY_CALENDAR)
                    .setMaxResults(maxResults)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            return events.getItems();

        } catch (IOException e) {
            log.error("Failed to fetch events for user {}", user.getId(), e);
            throw new GoogleCalendarException("Failed to fetch calendar events");
        }
    }

    private Calendar getCalendarService(User user) throws IOException {
        Credential credential = oAuth2Service.getCredentials(user.getId());

        return new Calendar.Builder(netHttpTransport, jsonFactory, credential)
                .setApplicationName("Slotly")
                .build();
    }

    private Event buildEvent(GoogleEventRequest request) {
        Event event = new Event()
                .setSummary(request.getSummary())
                .setDescription(request.getDescription());

        event.setStart(createEventDateTime(request.getStartTime(), request.getTimeZone()));
        event.setEnd(createEventDateTime(request.getEndTime(), request.getTimeZone()));

        return event;
    }

    private void updateEventDetails(Event event, GoogleEventRequest request) {
        event.setSummary(request.getSummary());
        event.setDescription(request.getDescription());
        event.setStart(createEventDateTime(request.getStartTime(), request.getTimeZone()));
        event.setEnd(createEventDateTime(request.getEndTime(), request.getTimeZone()));
    }

    private EventDateTime createEventDateTime(OffsetDateTime dateTime, String timeZone) {
        return new EventDateTime()
                .setDateTime(new DateTime(dateTime.toInstant().toEpochMilli()))
                .setTimeZone(timeZone);
    }
}
