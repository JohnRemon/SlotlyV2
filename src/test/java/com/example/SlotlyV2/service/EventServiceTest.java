package com.example.SlotlyV2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.common.exception.event.EventNotFoundException;
import com.example.SlotlyV2.common.exception.event.InvalidEventException;
import com.example.SlotlyV2.feature.availability.AvailabilityRules;
import com.example.SlotlyV2.feature.availability.AvailabilityRulesDTO;
import com.example.SlotlyV2.feature.email.event.EventCancelledEvent;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.EventRepository;
import com.example.SlotlyV2.feature.event.EventService;
import com.example.SlotlyV2.feature.event.dto.EventRequest;
import com.example.SlotlyV2.feature.event.dto.EventResponse;
import com.example.SlotlyV2.feature.slot.Slot;
import com.example.SlotlyV2.feature.slot.SlotService;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserService;

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

    private static AvailabilityRulesDTO publicRulesDto() {
        AvailabilityRulesDTO rules = AvailabilityRulesDTO.builder()
                .slotDurationMinutes(60)
                .maxSlotsPerUser(2)
                .allowsCancellations(true)
                .isPublic(true)
                .build();
        return rules;
    }

    private static AvailabilityRules rules(boolean isPublic) {
        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(60);
        rules.setMaxSlotsPerUser(2);
        rules.setAllowsCancellations(true);
        rules.setIsPublic(isPublic);
        return rules;
    }

    private static User user(long id) {
        User u = new User();
        u.setId(id);
        return u;
    }

    @Test
    void shouldCreateEventSuccessfully() {
        AvailabilityRulesDTO rules = publicRulesDto();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusHours(12);
        LocalDateTime endTime = now.plusHours(14);

        EventRequest request = EventRequest.builder()
                .eventName("Test Event")
                .eventStart(startTime)
                .eventEnd(endTime)
                .timeZone("Europe/Berlin")
                .rules(rules)
                .build();

        User host = user(1L);

        when(userService.getCurrentUser()).thenReturn(host);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event e = invocation.getArgument(0);
            e.setId(1L);
            return e;
        });

        Event event = eventService.createEvent(request);

        assertNotNull(event);
        assertEquals(1L, event.getId());
        assertEquals(host, event.getHost());
        assertEquals("Test Event", event.getEventName());
        assertEquals(startTime, event.getEventStart());
        assertEquals(endTime, event.getEventEnd());
        assertEquals("Europe/Berlin", event.getTimeZone());

        assertNotNull(event.getRules());
        assertEquals(60, event.getRules().getSlotDurationMinutes());
        assertEquals(2, event.getRules().getMaxSlotsPerUser());
        assertEquals(true, event.getRules().getAllowsCancellations());
        assertEquals(true, event.getRules().getIsPublic());

        verify(userService).getCurrentUser();
        verify(eventRepository).save(any(Event.class));
        verify(slotService).generateSlots(event);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void shouldThrowInvalidEventExceptionWhenEventNotInFuture() {
        AvailabilityRulesDTO rules = publicRulesDto();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusHours(1);
        LocalDateTime endTime = now.plusHours(1);

        EventRequest request = EventRequest.builder()
                .eventStart(startTime)
                .eventEnd(endTime)
                .timeZone("Europe/Berlin")
                .rules(rules)
                .build();

        when(userService.getCurrentUser()).thenReturn(user(1L));

        assertThrows(InvalidEventException.class, () -> eventService.createEvent(request));

        verify(eventRepository, never()).save(any());
        verify(slotService, never()).generateSlots(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void shouldThrowInvalidEventExceptionWhenEndBeforeOrEqualStart() {
        AvailabilityRulesDTO rules = publicRulesDto();

        LocalDateTime startTime = LocalDateTime.now().plusHours(12);

        EventRequest req1 = EventRequest.builder()
                .eventStart(startTime)
                .eventEnd(startTime.minusHours(1))
                .timeZone("Europe/Berlin")
                .rules(rules)
                .build();

        EventRequest req2 = EventRequest.builder()
                .eventStart(startTime)
                .eventEnd(startTime)
                .timeZone("Europe/Berlin")
                .rules(rules)
                .build();

        when(userService.getCurrentUser()).thenReturn(user(1L));

        assertThrows(InvalidEventException.class, () -> eventService.createEvent(req1));
        assertThrows(InvalidEventException.class, () -> eventService.createEvent(req2));

        verify(eventRepository, never()).save(any());
        verify(slotService, never()).generateSlots(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void shouldGetCurrentUserEventsSuccessfully() {
        User host = user(1L);

        Event event1 = new Event();
        event1.setId(1L);
        event1.setEventName("Event 1");
        event1.setHost(host);
        event1.setRules(rules(true));

        Event event2 = new Event();
        event2.setId(2L);
        event2.setEventName("Event 2");
        event2.setHost(host);
        event2.setRules(rules(true));

        when(eventRepository.findByHost(host)).thenReturn(new ArrayList<>(List.of(event1, event2)));

        List<EventResponse> events = eventService.getEvents(host);

        assertNotNull(events);
        assertEquals(2, events.size());
        assertEquals("Event 1", events.get(0).getEventName());
        assertEquals("Event 2", events.get(1).getEventName());

        verify(eventRepository).findByHost(host);
    }

    @Test
    void shouldGetEventByIdSuccessfullyForOwner() {
        User host = user(1L);

        Event event = new Event();
        event.setId(1L);
        event.setHost(host);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userService.getCurrentUser()).thenReturn(host);

        Event fetchedEvent = eventService.getEventById(1L);

        assertNotNull(fetchedEvent);
        assertEquals(1L, fetchedEvent.getId());

        verify(eventRepository).findById(1L);
        verify(userService).getCurrentUser();
    }

    @Test
    void shouldThrowUnauthorizedAccessExceptionWhenAccessingOtherUsersEvent() {
        User host = user(1L);
        User other = user(2L);

        Event event = new Event();
        event.setId(1L);
        event.setHost(host);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userService.getCurrentUser()).thenReturn(other);

        assertThrows(UnauthorizedAccessException.class, () -> eventService.getEventById(1L));

        verify(eventRepository).findById(1L);
        verify(userService).getCurrentUser();
    }

    @Test
    void shouldThrowEventNotFoundExceptionWhenEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.getEventById(1L));

        verify(eventRepository).findById(1L);
    }

    @Test
    void shouldDeleteEventAndPublishCancellationEvent() {
        User host = user(1L);

        Event event = new Event();
        event.setId(1L);
        event.setEventName("Event 1");
        event.setHost(host);

        Slot slot1 = mock(Slot.class);
        Slot slot2 = mock(Slot.class);
        when(slot1.getBookedByEmail()).thenReturn("a@test.com");
        when(slot2.getBookedByEmail()).thenReturn("b@test.com");
        event.setSlots(List.of(slot1, slot2));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userService.getCurrentUser()).thenReturn(host);

        eventService.deleteEventById(1L);

        InOrder order = inOrder(eventRepository, eventPublisher);
        order.verify(eventRepository).delete(event);

        ArgumentCaptor<EventCancelledEvent> captor = ArgumentCaptor.forClass(EventCancelledEvent.class);
        order.verify(eventPublisher).publishEvent(captor.capture());

        EventCancelledEvent published = captor.getValue();
        assertNotNull(published);
        assertEquals(1L, published.getEventCancelledEmailDTO().getEventId());
        assertEquals("Event 1", published.getEventCancelledEmailDTO().getEventName());
        assertEquals(2, published.getEventCancelledEmailDTO().getAttendeeEmails().size());
    }

    @Test
    void shouldThrowUnauthorizedAccessExceptionWhenDeletingOtherUsersEvent() {
        User host = user(1L);
        User other = user(2L);

        Event event = new Event();
        event.setId(1L);
        event.setHost(host);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userService.getCurrentUser()).thenReturn(other);

        assertThrows(UnauthorizedAccessException.class, () -> eventService.deleteEventById(1L));

        verify(eventRepository, never()).delete(any(Event.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void shouldThrowEventNotFoundExceptionWhenDeletingNonExistentEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.deleteEventById(1L));

        verify(eventRepository).findById(1L);
        verify(eventRepository, never()).delete(any(Event.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void shouldGetEventByShareableIdSuccessfullyWhenPublic() {
        Event event = new Event();
        event.setId(1L);
        event.setRules(rules(true));

        when(eventRepository.findByShareableId("abc")).thenReturn(Optional.of(event));

        Event fetched = eventService.getEventByShareableId("abc");

        assertNotNull(fetched);
        assertEquals(1L, fetched.getId());
        verify(eventRepository).findByShareableId("abc");
    }

    @Test
    void shouldThrowEventNotFoundExceptionWhenEventNotFoundByShareableId() {
        when(eventRepository.findByShareableId("missing")).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.getEventByShareableId("missing"));

        verify(eventRepository).findByShareableId("missing");
    }

    @Test
    void shouldThrowUnauthorizedAccessExceptionWhenAccessingPrivateEventByShareableId() {
        Event event = new Event();
        event.setRules(rules(false));

        when(eventRepository.findByShareableId("private")).thenReturn(Optional.of(event));

        assertThrows(UnauthorizedAccessException.class, () -> eventService.getEventByShareableId("private"));

        verify(eventRepository).findByShareableId("private");
    }
}
