package com.example.SlotlyV2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.example.SlotlyV2.dto.AvailabilityRulesDTO;
import com.example.SlotlyV2.dto.EventRequest;
import com.example.SlotlyV2.dto.EventResponse;
import com.example.SlotlyV2.event.EventCancelledEvent;
import com.example.SlotlyV2.exception.EventNotFoundException;
import com.example.SlotlyV2.exception.InvalidEventException;
import com.example.SlotlyV2.exception.UnauthorizedAccessException;
import com.example.SlotlyV2.model.AvailabilityRules;
import com.example.SlotlyV2.model.Event;
import com.example.SlotlyV2.model.Slot;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.repository.EventRepository;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SlotService slotService;

    @Mock
    private UserService userService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EventService eventService;

    @Test
    void shouldCreateEventSuccessfully() {
        // Arrange
        AvailabilityRulesDTO rules = new AvailabilityRulesDTO();
        rules.setSlotDurationMinutes(60);
        rules.setMaxSlotsPerUser(2);
        rules.setAllowsCancellations(true);
        rules.setIsPublic(true);

        LocalDateTime startTime = LocalDateTime.now().plusHours(12);
        LocalDateTime endTime = LocalDateTime.now().plusHours(14);

        EventRequest request = new EventRequest();
        request.setEventName("Test Event");
        request.setEventStart(startTime);
        request.setEventEnd(endTime);
        request.setTimeZone("Europe/Berlin");
        request.setRules(rules);

        User mockUser = new User();
        mockUser.setId(1L);

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(eventRepository.save(any(Event.class))).thenAnswer(
                invocation -> {
                    Event eventArgument = invocation.getArgument(0);
                    eventArgument.setId(1L);
                    return eventArgument;
                });

        // Act
        Event event = eventService.createEvent(request);

        // Assert - Basic Fields
        assertNotNull(event);
        assertEquals(1L, event.getId());
        assertEquals(mockUser, event.getHost());
        assertEquals("Test Event", event.getEventName());
        assertEquals(startTime, event.getEventStart());
        assertEquals(endTime, event.getEventEnd());
        assertEquals("Europe/Berlin", event.getTimeZone());

        assertNotNull(event.getRules());
        assertEquals(60, event.getRules().getSlotDurationMinutes());
        assertEquals(2, event.getRules().getMaxSlotsPerUser());
        assertEquals(true, event.getRules().getAllowsCancellations());
        assertEquals(true, event.getRules().getIsPublic());

        // Verify
        verify(userService).getCurrentUser();
        verify(eventRepository).save(any(Event.class));
        verify(slotService).generateSlots(event);
    }

    @Test
    void shouldThrowInvalidEventExceptionWhenEventNotInFuture() {
        // Arrange
        AvailabilityRulesDTO rules = new AvailabilityRulesDTO();
        rules.setSlotDurationMinutes(60);
        rules.setMaxSlotsPerUser(2);
        rules.setAllowsCancellations(true);
        rules.setIsPublic(true);

        LocalDateTime startTime = LocalDateTime.now().minusHours(12);
        LocalDateTime endTime = LocalDateTime.now().plusHours(14);

        EventRequest request = new EventRequest();
        request.setEventName("Test Event");
        request.setEventStart(startTime);
        request.setEventEnd(endTime);
        request.setTimeZone("Europe/Berlin");
        request.setRules(rules);

        User mockUser = new User();
        mockUser.setId(1L);

        when(userService.getCurrentUser()).thenReturn(mockUser);

        // Act & Assert
        assertThrows(InvalidEventException.class, () -> eventService.createEvent(request));

        // Verify
        verify(userService).getCurrentUser();
        verify(eventRepository, never()).save(any(Event.class));
        verify(slotService, never()).generateSlots(any(Event.class));
    }

    @Test
    void shouldThrowInvalidEventExceptionWhenEndBeforeStart() {
        // Arrange
        AvailabilityRulesDTO rules = new AvailabilityRulesDTO();
        rules.setSlotDurationMinutes(60);
        rules.setMaxSlotsPerUser(2);
        rules.setAllowsCancellations(true);
        rules.setIsPublic(true);

        LocalDateTime startTime = LocalDateTime.now().plusHours(12);
        LocalDateTime endTime = startTime.minusHours(1);

        EventRequest request = new EventRequest();
        request.setEventName("Test Event");
        request.setEventStart(startTime);
        request.setEventEnd(endTime);
        request.setTimeZone("Europe/Berlin");
        request.setRules(rules);

        User mockUser = new User();
        mockUser.setId(1L);

        when(userService.getCurrentUser()).thenReturn(mockUser);

        // Act & Assert
        assertThrows(InvalidEventException.class, () -> eventService.createEvent(request));

        // Verify
        verify(userService).getCurrentUser();
        verify(eventRepository, never()).save(any(Event.class));
        verify(slotService, never()).generateSlots(any(Event.class));
    }

    @Test
    void shouldThrowInvalidEventExceptionWhenEventEndEqualsStart() {
        // Arrange
        AvailabilityRulesDTO rules = new AvailabilityRulesDTO();
        rules.setSlotDurationMinutes(60);
        rules.setMaxSlotsPerUser(2);
        rules.setAllowsCancellations(true);
        rules.setIsPublic(true);

        LocalDateTime startTime = LocalDateTime.now().plusHours(12);
        LocalDateTime endTime = startTime; // End equals start

        EventRequest request = new EventRequest();
        request.setEventName("Test Event");
        request.setEventStart(startTime);
        request.setEventEnd(endTime);
        request.setTimeZone("Europe/Berlin");
        request.setRules(rules);

        User mockUser = new User();
        mockUser.setId(1L);

        when(userService.getCurrentUser()).thenReturn(mockUser);

        // Act & Assert
        assertThrows(InvalidEventException.class, () -> eventService.createEvent(request));

        // Verify
        verify(userService).getCurrentUser();
        verify(eventRepository, never()).save(any(Event.class));
        verify(slotService, never()).generateSlots(any(Event.class));
    }

    @Test
    void shouldGetCurrentUserEventsSuccessfully() {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);

        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(60);
        rules.setMaxSlotsPerUser(2);
        rules.setAllowsCancellations(true);
        rules.setIsPublic(true);

        List<Event> mockEvents = new ArrayList<>();

        Event event1 = new Event();
        event1.setId(1L);
        event1.setEventName("Event 1");
        event1.setHost(mockUser);
        event1.setRules(rules);
        mockEvents.add(event1);

        Event event2 = new Event();
        event2.setId(2L);
        event2.setEventName("Event 2");
        event2.setHost(mockUser);
        event2.setRules(rules);
        mockEvents.add(event2);

        when(eventRepository.findByHost(mockUser)).thenReturn(mockEvents);

        // Act
        List<EventResponse> events = eventService.getEvents(mockUser);

        // Assert
        assertNotNull(events);
        assertEquals(2, events.size());
        assertEquals("Event 1", events.get(0).getEventName());
        assertEquals("Event 2", events.get(1).getEventName());

        // Verify
        verify(eventRepository).findByHost(mockUser);
    }

    @Test
    void shouldGetEventByIdSuccessfully() {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);

        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(60);
        rules.setMaxSlotsPerUser(2);
        rules.setAllowsCancellations(true);
        rules.setIsPublic(true);

        Event event = new Event();
        event.setId(1L);
        event.setEventName("Event 1");
        event.setHost(mockUser);
        event.setRules(rules);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        // Act
        Event fetchedEvent = eventService.getEventById(1L);

        // Assert
        assertNotNull(fetchedEvent);
        assertEquals(event.getId(), fetchedEvent.getId());
        assertEquals(event.getEventName(), fetchedEvent.getEventName());
        assertEquals(event.getHost(), mockUser);

        // Verify
        verify(eventRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenEventNotFound() {
        // Arrange
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EventNotFoundException.class, () -> eventService.getEventById(anyLong()));

        verify(eventRepository).findById(anyLong());
    }

    @Test
    void shouldDeleteEventandCascadeDeleteAllSlotsSuccessfully() {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);

        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(60);
        rules.setMaxSlotsPerUser(2);
        rules.setAllowsCancellations(true);
        rules.setIsPublic(true);

        Event event = new Event();
        event.setId(1L);
        event.setEventName("Event 1");
        event.setHost(mockUser);
        event.setRules(rules);

        Slot slot1 = new Slot();
        slot1.setId(1L);
        slot1.setEvent(event);
        slot1.setStartTime(LocalDateTime.now());
        slot1.setEndTime(LocalDateTime.now().plusHours(1));

        Slot slot2 = new Slot();
        slot2.setId(2L);
        slot2.setEvent(event);
        slot2.setStartTime(LocalDateTime.now().plusHours(1));
        slot2.setEndTime(LocalDateTime.now().plusHours(2));

        event.setSlots(Arrays.asList(slot1, slot2));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userService.getCurrentUser()).thenReturn(mockUser);
        doNothing().when(eventRepository).deleteById(1L);

        // Act
        eventService.deleteEventById(1L);

        // Assert
        verify(eventRepository).findById(1L);
        verify(userService).getCurrentUser();
        verify(eventRepository).deleteById(1L);
        verifyNoMoreInteractions(eventRepository, slotService, userService);

        // Assert - Event publish
        ArgumentCaptor<EventCancelledEvent> eventCaptor = ArgumentCaptor.forClass(EventCancelledEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        EventCancelledEvent publishedEvent = eventCaptor.getValue();
        assertNotNull(publishedEvent);
        assertEquals(1L, publishedEvent.getEventCancelledEmailDTO().getEventId());
        assertEquals("Event 1", publishedEvent.getEventCancelledEmailDTO().getEventName());
        assertEquals(2, publishedEvent.getEventCancelledEmailDTO().getAttendeeEmails().size());
    }

    @Test
    void shouldThrowUnauthorizedAccessExceptionWhenDeletingOtherUsersEvent() {
        // Arrange
        User eventHost = new User();
        eventHost.setId(1L);

        User currentUser = new User();
        currentUser.setId(2L);

        Event event = new Event();
        event.setId(1L);
        event.setEventName("Event 1");
        event.setHost(eventHost);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userService.getCurrentUser()).thenReturn(currentUser);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class,
                () -> eventService.deleteEventById(1L));

        verify(eventRepository).findById(1L);
        verify(userService).getCurrentUser();
        verify(eventRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldThrowEventNotFoundExceptionWhenDeletingNonExistentEvent() {
        // Arrange
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EventNotFoundException.class,
                () -> eventService.deleteEventById(anyLong()));

        verify(eventRepository).findById(anyLong());
        verify(eventRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldGetEventByShareableIdSuccessfully() {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);

        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(60);
        rules.setMaxSlotsPerUser(2);
        rules.setAllowsCancellations(true);
        rules.setIsPublic(true);

        Event event = new Event();
        event.setId(1L);
        event.setEventName("Event 1");
        event.setHost(mockUser);
        event.setRules(rules);

        when(eventRepository.findByShareableId(anyString())).thenReturn(Optional.of(event));

        // Act
        Event fetchedEvent = eventService.getEventByShareableId(anyString());

        // Assert - Basic Fields
        assertNotNull(fetchedEvent);
        assertEquals(1L, fetchedEvent.getId());
        assertEquals(mockUser, fetchedEvent.getHost());
        assertEquals("Event 1", fetchedEvent.getEventName());
    }

    @Test
    void shouldThrowExceptionWhenEventNotFoundByShareableId() {
        // Arrange
        when(eventRepository.findByShareableId(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EventNotFoundException.class, () -> eventService.getEventByShareableId(anyString()));

        verify(eventRepository).findByShareableId(anyString());
    }
}
