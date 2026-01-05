package com.example.SlotlyV2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.example.SlotlyV2.dto.SlotRequest;
import com.example.SlotlyV2.event.SlotBookedEvent;
import com.example.SlotlyV2.exception.EventNotFoundException;
import com.example.SlotlyV2.exception.InvalidSlotException;
import com.example.SlotlyV2.exception.MaxCapacityExceededException;
import com.example.SlotlyV2.exception.SlotAlreadyBookedException;
import com.example.SlotlyV2.exception.SlotNotFoundException;
import com.example.SlotlyV2.model.AvailabilityRules;
import com.example.SlotlyV2.model.Event;
import com.example.SlotlyV2.model.Slot;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.repository.EventRepository;
import com.example.SlotlyV2.repository.SlotRepository;

@ExtendWith(MockitoExtension.class)
public class SlotServiceTest {

    @Mock
    private SlotRepository slotRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SlotService slotService;

    @BeforeEach
    void setUp() {
        reset(slotRepository, eventRepository, eventPublisher);
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldGenerateSlotsWithinEventTimeBoundsAndPersistThem() {
        // Arrange
        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(30);

        Event event = new Event();
        event.setId(1L);
        event.setRules(rules);

        LocalDateTime eventStart = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime eventEnd = LocalDateTime.of(2025, 1, 1, 12, 0);

        event.setEventStart(eventStart);
        event.setEventEnd(eventEnd);

        when(slotRepository.saveAll(any(List.class))).thenAnswer(invocation -> {
            List<Slot> slots = invocation.getArgument(0);

            assertNotNull(slots);
            assertFalse(slots.isEmpty());

            slots.forEach(slot -> {
                assertEquals(event, slot.getEvent());
                assertFalse(slot.getStartTime().isBefore(eventStart));
                assertFalse(slot.getEndTime().isAfter(eventEnd));
                assertEquals(null, slot.getBookedByEmail());
                assertEquals(null, slot.getBookedByName());
            });

            return slots;
        });

        // Act
        slotService.generateSlots(event);

        // Assert
        verify(slotRepository).saveAll(any(List.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldGenerateSlotWhenEndTimeIsExactlyEqualToSlotEnd() {
        // Arrange
        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(60);

        Event event = new Event();
        event.setId(1L);
        event.setRules(rules);

        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 1, 10, 0);

        event.setEventStart(start);
        event.setEventEnd(end);

        when(slotRepository.saveAll(any(List.class))).thenAnswer(invocation -> {
            List<Slot> slots = invocation.getArgument(0);

            assertEquals(1, slots.size());

            Slot slot = slots.get(0);
            assertEquals(start, slot.getStartTime());
            assertEquals(end, slot.getEndTime());
            assertEquals(event, slot.getEvent());

            return slots;
        });

        // Act
        slotService.generateSlots(event);

        // Assert
        verify(slotRepository).saveAll(any(List.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldGenerateNoSlotsWhenSlotDurationExceedsEventDuration() {
        // Arrange
        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(90);

        Event event = new Event();
        event.setId(1L);
        event.setRules(rules);

        event.setEventStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        event.setEventEnd(LocalDateTime.of(2025, 1, 1, 11, 0));

        when(slotRepository.saveAll(any(List.class))).thenAnswer(invocation -> {
            List<Slot> slots = invocation.getArgument(0);

            assertNotNull(slots);
            assertEquals(0, slots.size());

            return slots;
        });

        // Act
        slotService.generateSlots(event);

        // Assert
        verify(slotRepository).saveAll(any(List.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldNotGenerateOverlappingSlots() {
        // Arrange
        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(30);

        Event event = new Event();
        event.setId(1L);
        event.setRules(rules);

        event.setEventStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        event.setEventEnd(LocalDateTime.of(2025, 1, 1, 12, 0));

        when(slotRepository.saveAll(any(List.class))).thenAnswer(invocation -> {
            List<Slot> slots = invocation.getArgument(0);

            for (int i = 0; i < slots.size() - 1; i++) {
                Slot current = slots.get(i);
                Slot next = slots.get(i + 1);

                assertFalse(current.getEndTime().isAfter(next.getStartTime()));
            }

            return slots;
        });

        // Act
        slotService.generateSlots(event);

        // Assert
        verify(slotRepository).saveAll(any(List.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldCallSaveAllOnceWhenGeneratingSlots() {
        // Arrange
        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(30);

        Event event = new Event();
        event.setId(1L);
        event.setRules(rules);

        event.setEventStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        event.setEventEnd(LocalDateTime.of(2025, 1, 1, 11, 0));

        // Act
        slotService.generateSlots(event);

        // Assert
        verify(slotRepository).saveAll(any(List.class));
    }

    @Test
    void shouldBookSlotSuccessfully() {
        // Arrange
        User host = new User();
        host.setId(1L);
        host.setEmail("host@example.com");
        host.setFirstName("John");
        host.setLastName("Doe");

        AvailabilityRules rules = new AvailabilityRules();
        rules.setMaxCapacity(10);

        Event event = new Event();
        event.setId(1L);
        event.setRules(rules);
        event.setHost(host);

        LocalDateTime startTime = LocalDateTime.now().plusHours(1);

        Slot slot = new Slot();
        slot.setId(1L);
        slot.setEvent(event);
        slot.setStartTime(startTime);
        slot.setBookedByName(null);
        slot.setBookedByEmail(null);

        SlotRequest request = new SlotRequest();
        request.setEventId(event.getId());
        request.setStartTime(startTime);
        request.setAttendeeEmail("attendee@example.com");
        request.setAttendeeName("Jane Smith");

        when(slotRepository.findByEventIdAndStartTime(event.getId(), startTime)).thenReturn(Optional.of(slot));
        when(slotRepository.countByEventAndBookedByEmailIsNotNullAndBookedByNameIsNotNull(event)).thenReturn(0);
        when(slotRepository.save(any(Slot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Slot bookedSlot = slotService.bookSlot(request);

        // Assert
        assertNotNull(bookedSlot);
        assertEquals(request.getAttendeeEmail(), bookedSlot.getBookedByEmail());
        assertEquals(request.getAttendeeName(), bookedSlot.getBookedByName());
        assertFalse(bookedSlot.isAvailable());

        verify(slotRepository).findByEventIdAndStartTime(event.getId(), startTime);
        verify(slotRepository).countByEventAndBookedByEmailIsNotNullAndBookedByNameIsNotNull(event);
        verify(slotRepository).save(any(Slot.class));

        // Assert - Event Publish
        ArgumentCaptor<SlotBookedEvent> eventCaptor = ArgumentCaptor.forClass(SlotBookedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        SlotBookedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(capturedEvent.getBookingEmailDTO().getSlotId(), slot.getId());
        assertEquals(capturedEvent.getBookingEmailDTO().getStartTime(), startTime);
        assertEquals(capturedEvent.getBookingEmailDTO().getHostEmail(), host.getEmail());
        assertEquals(capturedEvent.getBookingEmailDTO().getAttendeeEmail(), request.getAttendeeEmail());
        assertEquals(capturedEvent.getBookingEmailDTO().getAttendeeName(), request.getAttendeeName());
    }

    @Test
    void shouldThrowSlotNotFoundExceptionWhenSlotNotFoundByEventIdAndStartTime() {
        // Arrange
        SlotRequest request = new SlotRequest();
        request.setEventId(1L);
        request.setStartTime(LocalDateTime.now().plusHours(1));
        when(slotRepository.findByEventIdAndStartTime(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SlotNotFoundException.class, () -> slotService.bookSlot(request));

        verify(slotRepository).findByEventIdAndStartTime(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void shouldThrowInvalidSlotExceptionWhenSlotDoesNotBelongToEvent() {
        // Arrange
        Event event = new Event();
        event.setId(1L);

        Slot slot = new Slot();
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        slot.setId(1L);
        slot.setEvent(event);
        slot.setStartTime(startTime);

        SlotRequest request = new SlotRequest();
        request.setEventId(2L);
        request.setStartTime(startTime);

        when(slotRepository.findByEventIdAndStartTime(request.getEventId(), request.getStartTime()))
                .thenReturn(Optional.of(slot));

        // Act & Assert
        assertThrows(InvalidSlotException.class, () -> slotService.bookSlot(request));

        verify(slotRepository).findByEventIdAndStartTime(request.getEventId(), request.getStartTime());
    }

    @Test
    void shouldThrowSlotAlreadyBookedException() {
        // Arrange
        AvailabilityRules rules = new AvailabilityRules();
        rules.setMaxCapacity(2);

        Event event = new Event();
        event.setId(1L);
        event.setRules(rules);

        LocalDateTime startTime = LocalDateTime.now().plusHours(1);

        Slot slot = new Slot();
        slot.setId(1L);
        slot.setEvent(event);
        slot.setStartTime(startTime);
        slot.setBookedByName("John Doe");
        slot.setBookedByEmail("test@example.com");

        SlotRequest request = new SlotRequest();
        request.setEventId(1L);
        request.setStartTime(startTime);

        when(slotRepository.findByEventIdAndStartTime(request.getEventId(), request.getStartTime()))
                .thenReturn(Optional.of(slot));

        // Act & Assert
        assertThrows(SlotAlreadyBookedException.class, () -> slotService.bookSlot(request));

        verify(slotRepository).findByEventIdAndStartTime(request.getEventId(), request.getStartTime());
    }

    @Test
    void shouldThrowMaxCapacityExceededException() {
        // Arrange
        AvailabilityRules rules = new AvailabilityRules();
        rules.setMaxCapacity(1);

        Event event = new Event();
        event.setId(1L);
        event.setRules(rules);

        LocalDateTime startTime = LocalDateTime.now().plusHours(1);

        Slot slot = new Slot();
        slot.setId(1L);
        slot.setEvent(event);
        slot.setStartTime(startTime);
        slot.setBookedByEmail(null);
        slot.setBookedByName(null);

        SlotRequest request = new SlotRequest();
        request.setEventId(1L);
        request.setStartTime(startTime);

        when(slotRepository.findByEventIdAndStartTime(request.getEventId(), request.getStartTime()))
                .thenReturn(Optional.of(slot));
        when(slotRepository.countByEventAndBookedByEmailIsNotNullAndBookedByNameIsNotNull(event)).thenReturn(1);

        // Act & Assert
        assertThrows(MaxCapacityExceededException.class, () -> slotService.bookSlot(request));

        verify(slotRepository).countByEventAndBookedByEmailIsNotNullAndBookedByNameIsNotNull(event);
    }

    @Test
    void shouldGetSlotsByEventIdSuccessfully() {
        // Arrange
        Event event = new Event();
        event.setId(1L);

        Slot slot = new Slot();
        slot.setId(1L);

        when(slotRepository.findByEventId(event.getId())).thenReturn(List.of(slot));

        // Act
        List<Slot> slots = slotService.getSlots(event.getId());

        // Assert
        assertNotNull(slots);
        assertEquals(1, slots.size());
        assertEquals(slot.getId(), slots.get(0).getId());

        verify(slotRepository).findByEventId(event.getId());
    }

    @Test
    void shouldReturnEmptyListWhenNoSlotsExistForEvent() {
        // Arrange
        Event event = new Event();
        event.setId(1L);

        when(slotRepository.findByEventId(event.getId())).thenReturn(List.of());

        // Act
        List<Slot> slots = slotService.getSlots(event.getId());

        // Assert
        assertNotNull(slots);
        assertEquals(0, slots.size());

        verify(slotRepository).findByEventId(event.getId());
    }

    @Test
    void shouldGetUserBookedSlotsSuccessfully() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Slot slot = new Slot();
        slot.setId(1L);

        when(slotRepository.findByBookedByEmail(user.getEmail())).thenReturn(List.of(slot));

        // Act
        List<Slot> slots = slotService.getBookedSlots(user);

        // Assert
        assertNotNull(slots);
        assertEquals(1, slots.size());
        assertEquals(slot.getId(), slots.get(0).getId());
        assertEquals(slot.getEvent(), slots.get(0).getEvent());
        assertEquals(slot.getStartTime(), slots.get(0).getStartTime());

        verify(slotRepository).findByBookedByEmail(user.getEmail());
    }

    @Test
    void shouldGetAvailableSlotsByShareableIdSuccessfully() {
        // Arrange
        Event event = new Event();
        event.setId(1L);
        event.setShareableId("event1");

        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        Slot slot = new Slot();
        slot.setId(1L);
        slot.setEvent(event);
        slot.setStartTime(startTime);
        slot.setBookedByEmail(null);
        slot.setBookedByName(null);

        when(eventRepository.findByShareableId(event.getShareableId())).thenReturn(Optional.of(event));
        when(slotRepository.findByEventAndBookedByEmailIsNullAndBookedByNameIsNull(event))
                .thenReturn(List.of(slot));

        // Act
        List<Slot> slots = slotService.getAvailableSlotsByShareableId(event.getShareableId());

        // Assert
        assertNotNull(slots);
        assertEquals(1, slots.size());
        assertEquals(slot.getId(), slots.get(0).getId());
        assertEquals(slot.getEvent(), slots.get(0).getEvent());
        assertEquals(slot.getStartTime(), slots.get(0).getStartTime());

        verify(eventRepository).findByShareableId(event.getShareableId());
        verify(slotRepository).findByEventAndBookedByEmailIsNullAndBookedByNameIsNull(event);
    }

    @Test
    void shouldThrowEventNotFoundException() {
        // Arrange
        when(eventRepository.findByShareableId(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EventNotFoundException.class, () -> slotService.getAvailableSlotsByShareableId(anyString()));

        verify(slotRepository, never()).countByEventAndBookedByEmailIsNotNullAndBookedByNameIsNotNull(any(Event.class));
    }

    @Test
    void shouldGetSlotByIdSuccessfully() {
        // Arrange
        Slot testSlot = createTestSlot();
        when(slotRepository.findById(anyLong())).thenReturn(Optional.of(testSlot));

        // Act
        Slot slot = slotService.getSlotById(1L);

        // Assert
        assertNotNull(slot);
        assertEquals(testSlot.getId(), slot.getId());
        assertEquals(testSlot.getEvent(), slot.getEvent());
        assertEquals(testSlot.getStartTime(), slot.getStartTime());
        assertEquals(testSlot.getEndTime(), slot.getEndTime());
    }

    @Test
    void shouldThrowSlotNotFoundExceptionWhenSlotNotFoundById() {
        // Arrange
        when(slotRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SlotNotFoundException.class, () -> slotService.getSlotById(anyLong()));

        verify(slotRepository).findById(anyLong());
    }

    private Slot createTestSlot() {
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

        Slot slot = new Slot();
        slot.setId(1L);
        slot.setEvent(event);
        slot.setStartTime(LocalDateTime.now().plusHours(1));
        slot.setEndTime(LocalDateTime.now().plusHours(2));

        return slot;
    }
}
