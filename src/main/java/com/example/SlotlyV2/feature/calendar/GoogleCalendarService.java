package com.example.SlotlyV2.feature.calendar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.config.GoogleCalendarConfig;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    private final NetHttpTransport netHttpTransport;
    private final JsonFactory jsonFactory;
    private final GoogleCalendarConfig config;

    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    private Credential getCredentials() throws IOException {
        InputStream in = GoogleCalendarService.class.getResourceAsStream(config.getCredentialsFilePath());

        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + config.getCredentialsFilePath());
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                netHttpTransport, jsonFactory, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(config.getTokensDirectoryPath())))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private Calendar getCalendarService() throws IOException {
        Credential credential = getCredentials();
        return new Calendar.Builder(netHttpTransport, jsonFactory, credential)
                .setApplicationName(config.getApplicationName())
                .build();
    }

    public List<Event> getUpcomingEvents(int maxResults) throws IOException {
        Calendar service = getCalendarService();
        DateTime now = new DateTime(System.currentTimeMillis());

        Events events = service.events().list("primary")
                .setMaxResults(maxResults)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems();
    }

    public Event createEvent(String summary, String description, LocalDateTime startTime, LocalDateTime endTime,
            String timeZone) throws IOException {

        Calendar service = getCalendarService();

        Event event = new Event()
                .setSummary(summary)
                .setDescription(description);

        DateTime startDate = new DateTime(
                Date.from(startTime.atZone(ZoneId.of(timeZone)).toInstant()));

        EventDateTime start = new EventDateTime()
                .setDateTime(startDate)
                .setTimeZone(timeZone);

        event.setStart(start);

        DateTime endDate = new DateTime(
                Date.from(endTime.atZone(ZoneId.of(timeZone)).toInstant()));

        EventDateTime end = new EventDateTime()
                .setDateTime(endDate)
                .setTimeZone(timeZone);

        event.setEnd(end);

        return service.events().insert("primary", event).execute();
    }

    public Event updateEvent(String eventId, String summary, String description, LocalDateTime startTime,
            LocalDateTime endTime,
            String timeZone) throws IOException {

        Calendar service = getCalendarService();

        Event event = service.events().get("primary", eventId).execute();
        event.setSummary(summary);
        event.setDescription(description);

        DateTime startDate = new DateTime(
                Date.from(startTime.atZone(ZoneId.of(timeZone)).toInstant()));

        event.getStart().setDateTime(startDate);

        DateTime endDate = new DateTime(
                Date.from(endTime.atZone(ZoneId.of(timeZone)).toInstant()));

        event.getEnd().setDateTime(endDate);

        return service.events().update("primary", eventId, event).execute();
    }

    public void deleteEvent(String eventId) throws IOException {
        Calendar service = getCalendarService();
        service.events().delete("primary", eventId).execute();
    }
}
