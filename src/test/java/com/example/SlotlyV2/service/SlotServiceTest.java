package com.example.SlotlyV2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.common.exception.event.EventNotFoundException;
import com.example.SlotlyV2.common.exception.event.MaxCapacityExceededException;
import com.example.SlotlyV2.common.exception.slot.InvalidSlotException;
import com.example.SlotlyV2.common.exception.slot.SlotAlreadyBookedException;
import com.example.SlotlyV2.common.exception.slot.SlotNotFoundException;
import com.example.SlotlyV2.common.util.NameUtils;
import com.example.SlotlyV2.common.util.SlotUtils;
import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.availability.AvailabilityRules;
import com.example.SlotlyV2.feature.email.event.SlotBookedEvent;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.EventRepository;
import com.example.SlotlyV2.feature.slot.Slot;
import com.example.SlotlyV2.feature.slot.SlotRepository;
import com.example.SlotlyV2.feature.slot.SlotService;
import com.example.SlotlyV2.feature.slot.dto.CancelBookingRequest;
import com.example.SlotlyV2.feature.slot.dto.SlotRequest;
import com.example.SlotlyV2.feature.user.User;

@ExtendWith(MockitoExtension.class)
public class SlotServiceTest {

    @Mock
    private SlotRepository slotRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private NameUtils nameUtils;

    @Mock
    private SlotUtils slotUtils;

    @Mock
    private TimeZoneConverter timeZoneConverter;

    @InjectMocks
    private SlotService slotService;

    private static final ZoneId EVENT_ZONE = ZoneId.of("Europe/Berlin");

    @BeforeEach
    void setUp() {
        reset(slotRepository, eventRepository, eventPublisher, nameUtils, slotUtils, timeZoneConverter);
    }

    @Test
    void shouldGenerateSlotsWithinEventTimeBoundsAndPersistThem() {
        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(30);

        Event event = new Event();
        event.setId(1L);
        event.setAvailabilityRules(rules);

        OffsetDateTime eventStart = OffsetDateTime.of(2025, 1, 1, 10, 0, 0, 0, java.time.ZoneOffset.ofHours(1));
        OffsetDateTime eventEnd = OffsetDateTime.of(2025, 1, 1, 12, 0, 0, 0, java.time.ZoneOffset.ofHours(1));

        event.setEventStart(eventStart);
        event.setEventEnd(eventEnd);

        Slot slot1 = Slot.builder()
                .event(event)
                .startTime(eventStart)
                .endTime(eventStart.plusMinutes(30))
                .build();
        Slot slot2 = Slot.builder()
                .event(event)
                .startTime(eventStart.plusMinutes(30))
                .endTime(eventStart.plusMinutes(60))
                .build();

        when(slotUtils.buildSlotsByTime(any(Event.class), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(List.of(slot1, slot2));

        when(slotRepository.saveAll(any(List.class))).thenAnswer(invocation -> invocation.getArgument(0));

        slotService.generateSlots(event);

        verify(slotRepository).saveAll(any(List.class));
    }

    @Test
    void shouldThrowInvalidSlotExceptionWhenSlotDurationIsZeroOrNegative() {
        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(0);

        Event event = new Event();
        event.setId(1L);
        event.setAvailabilityRules(rules);

        event.setEventStart(OffsetDateTime.of(2025, 1, 1, 10, 0, 0, 0, ZoneId.of("Europe/Berlin").getRules().getOffset(OffsetDateTime.now().toInstant())));
        event.setEventEnd(OffsetDateTime.of(2025, 1, 1, 11, 0, 0, 0, ZoneId.of("Europe/Berlin").getRules().getOffset(OffsetDateTime.now().toInstant())));

        assertThrows(InvalidSlotException.class, () -> slotService.generateSlots(event));
    }

    @Test
    void shouldGenerateSlotWhenEndTimeIsExactlyEqualToSlotEnd() {
        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(60);

        Event event = new Event();
        event.setId(1L);
        event.setAvailabilityRules(rules);

        OffsetDateTime start = OffsetDateTime.of(2025, 1, 1, 9, 0, 0, 0, java.time.ZoneOffset.ofHours(1));
        OffsetDateTime end = OffsetDateTime.of(2025, 1, 1, 10, 0, 0, 0, java.time.ZoneOffset.ofHours(1));

        event.setEventStart(start);
        event.setEventEnd(end);

        Slot slot = Slot.builder()
                .event(event)
                .startTime(start)
                .endTime(end)
                .build();

        when(slotUtils.buildSlotsByTime(any(Event.class), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(List.of(slot));

        when(slotRepository.saveAll(any(List.class))).thenAnswer(invocation -> invocation.getArgument(0));

        slotService.generateSlots(event);

        verify(slotRepository).saveAll(any(List.class));
    }

    @Test
    void shouldGenerateNoSlotsWhenSlotDurationExceedsEventDuration() {
        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(90);

        Event event = new Event();
        event.setId(1L);
        event.setAvailabilityRules(rules);

        event.setEventStart(OffsetDateTime.of(2025, 1, 1, 10, 0, 0, 0, ZoneId.of("Europe/Berlin").getRules().getOffset(OffsetDateTime.now().toInstant())));
        event.setEventEnd(OffsetDateTime.of(2025, 1, 1, 11, 0, 0, 0, ZoneId.of("Europe/Berlin").getRules().getOffset(OffsetDateTime.now().toInstant())));

        when(slotRepository.saveAll(any(List.class))).thenAnswer(invocation -> {
            List<Slot> slots = invocation.getArgument(0);

            assertNotNull(slots);
            assertEquals(0, slots.size());

            return slots;
        });

        slotService.generateSlots(event);

        verify(slotRepository).saveAll(any(List.class));
    }

    @Test
    void shouldNotGenerateOverlappingSlots() {
        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(30);

        Event event = new Event();
        event.setId(1L);
        event.setAvailabilityRules(rules);

        event.setEventStart(OffsetDateTime.of(2025, 1, 1, 10, 0, 0, 0, ZoneId.of("Europe/Berlin").getRules().getOffset(OffsetDateTime.now().toInstant())));
        event.setEventEnd(OffsetDateTime.of(2025, 1, 1, 12, 0, 0, 0, ZoneId.of("Europe/Berlin").getRules().getOffset(OffsetDateTime.now().toInstant())));

        when(slotRepository.saveAll(any(List.class))).thenAnswer(invocation -> {
            List<Slot> slots = invocation.getArgument(0);

            for (int i = 0; i < slots.size() - 1; i++) {
                Slot current = slots.get(i);
                Slot next = slots.get(i + 1);

                assertFalse(current.getEndTime().isAfter(next.getStartTime()));
            }

            return slots;
        });

        slotService.generateSlots(event);

        verify(slotRepository).saveAll(any(List.class));
    }

    @Test
    void shouldCallSaveAllOnceWhenGeneratingSlots() {
        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(30);

        Event event = new Event();
        event.setId(1L);
        event.setAvailabilityRules(rules);

        event.setEventStart(OffsetDateTime.of(2025, 1, 1, 10, 0, 0, 0, ZoneId.of("Europe/Berlin").getRules().getOffset(OffsetDateTime.now().toInstant())));
        event.setEventEnd(OffsetDateTime.of(2025, 1, 1, 11, 0, 0, 0, ZoneId.of("Europe/Berlin").getRules().getOffset(OffsetDateTime.now().toInstant())));

        slotService.generateSlots(event);

        verify(slotRepository).saveAll(any(List.class));
    }

    @Test
    void shouldBookSlotSuccessfully() {
        User host = new User();
        host.setId(1L);
        host.setEmail("host@example.com");
        host.setFirstName("John");
        host.setLastName("Doe");

        AvailabilityRules rules = new AvailabilityRules();
        rules.setMaxCapacity(10);

        Event event = new Event();
        event.setId(1L);
        event.setAvailabilityRules(rules);
        event.setHost(host);
        event.setTimeZone("Europe/Berlin");

        OffsetDateTime startTime = ZonedDateTime.now(EVENT_ZONE).plusHours(1).toOffsetDateTime();

        Slot slot = new Slot();
        slot.setId(1L);
        slot.setEvent(event);
        slot.setStartTime(startTime);
        slot.setBookedByName(null);
        slot.setBookedByEmail(null);

        SlotRequest request = SlotRequest.builder()
                .eventId(event.getId())
                .startTime(ZonedDateTime.now(EVENT_ZONE).plusHours(1).toLocalDateTime())
                .attendeeEmail("attendee@example.com")
                .attendeeName("Jane Smith")
                .build();

        when(timeZoneConverter.toUtc(any(LocalDateTime.class), anyString())).thenReturn(startTime);
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(slotRepository.findByEventIdAndStartTime(event.getId(), startTime)).thenReturn(Optional.of(slot));
        when(slotRepository.countByEventAndBookedByEmailIsNotNullAndBookedByNameIsNotNull(event)).thenReturn(0);
        when(slotRepository.save(any(Slot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Slot bookedSlot = slotService.bookSlot(request);

        assertNotNull(bookedSlot);
        assertEquals(request.getAttendeeEmail(), bookedSlot.getBookedByEmail());
        assertEquals(request.getAttendeeName(), bookedSlot.getBookedByName());
        assertFalse(bookedSlot.isAvailable());

        verify(eventRepository).findById(event.getId());
        verify(slotRepository).findByEventIdAndStartTime(event.getId(), startTime);
        verify(slotRepository).countByEventAndBookedByEmailIsNotNullAndBookedByNameIsNotNull(event);
        verify(slotRepository).save(any(Slot.class));

        ArgumentCaptor<SlotBookedEvent> eventCaptor = ArgumentCaptor.forClass(SlotBookedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        SlotBookedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(capturedEvent.getBookingEmailDTO().getSlotId(), slot.getId());
        assertEquals(capturedEvent.getBookingEmailDTO().getHostEmail(), host.getEmail());
        assertEquals(capturedEvent.getBookingEmailDTO().getAttendeeEmail(), request.getAttendeeEmail());
        assertEquals(capturedEvent.getBookingEmailDTO().getAttendeeName(), request.getAttendeeName());
    }

    @Test
    void shouldThrowSlotNotFoundExceptionWhenSlotNotFoundByEventIdAndStartTime() {
        LocalDateTime requestStartTime = ZonedDateTime.now(EVENT_ZONE).plusHours(1).toLocalDateTime();
        OffsetDateTime utcStartTime = ZonedDateTime.now(EVENT_ZONE).plusHours(1).toOffsetDateTime();

        SlotRequest request = SlotRequest.builder()
                .eventId(1L)
                .startTime(requestStartTime)
                .build();

        Event mockEvent = new Event();
        mockEvent.setId(1L);
        mockEvent.setTimeZone("Europe/Berlin");

        when(timeZoneConverter.toUtc(any(LocalDateTime.class), eq("Europe/Berlin"))).thenReturn(utcStartTime);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(mockEvent));
        when(slotRepository.findByEventIdAndStartTime(1L, utcStartTime))
                .thenReturn(Optional.empty());

        assertThrows(SlotNotFoundException.class, () -> slotService.bookSlot(request));

        verify(slotRepository).findByEventIdAndStartTime(1L, utcStartTime);
    }

    @Test
    void shouldThrowSlotAlreadyBookedException() {
        AvailabilityRules rules = new AvailabilityRules();
        rules.setMaxCapacity(2);

        Event event = new Event();
        event.setId(1L);
        event.setAvailabilityRules(rules);
        event.setTimeZone("Europe/Berlin");

        OffsetDateTime startTime = ZonedDateTime.now(EVENT_ZONE).plusHours(1).toOffsetDateTime();

        Slot slot = new Slot();
        slot.setId(1L);
        slot.setEvent(event);
        slot.setStartTime(startTime);
        slot.setBookedByName("John Doe");
        slot.setBookedByEmail("test@example.com");

        SlotRequest request = SlotRequest.builder()
                .eventId(1L)
                .startTime(ZonedDateTime.now(EVENT_ZONE).plusHours(1).toLocalDateTime())
                .build();

        when(timeZoneConverter.toUtc(any(LocalDateTime.class), anyString())).thenReturn(startTime);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(slotRepository.findByEventIdAndStartTime(request.getEventId(), startTime))
                .thenReturn(Optional.of(slot));

        assertThrows(SlotAlreadyBookedException.class, () -> slotService.bookSlot(request));

        verify(slotRepository).findByEventIdAndStartTime(request.getEventId(), startTime);
    }

    @Test
    void shouldThrowInvalidSlotExceptionWhenBookingPastSlot() {
        Event event = new Event();
        event.setTimeZone("Europe/Berlin");

        Slot slot = new Slot();
        slot.setEvent(event);
        slot.setStartTime(ZonedDateTime.now(EVENT_ZONE).minusHours(1).toOffsetDateTime());

        SlotRequest request = SlotRequest.builder()
                .eventId(1L)
                .startTime(ZonedDateTime.now(EVENT_ZONE).minusHours(1).toLocalDateTime())
                .build();

        when(timeZoneConverter.toUtc(any(LocalDateTime.class), anyString())).thenReturn(slot.getStartTime());
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(slotRepository.findByEventIdAndStartTime(request.getEventId(), slot.getStartTime()))
                .thenReturn(Optional.of(slot));

        assertThrows(InvalidSlotException.class, () -> slotService.bookSlot(request));
    }

    @Test
    void shouldThrowMaxCapacityExceededException() {
        AvailabilityRules rules = new AvailabilityRules();
        rules.setMaxCapacity(1);

        Event event = new Event();
        event.setId(1L);
        event.setAvailabilityRules(rules);
        event.setTimeZone("Europe/Berlin");

        OffsetDateTime startTime = ZonedDateTime.now(EVENT_ZONE).plusHours(1).toOffsetDateTime();

        Slot slot = new Slot();
        slot.setId(1L);
        slot.setEvent(event);
        slot.setStartTime(startTime);
        slot.setBookedByEmail(null);
        slot.setBookedByName(null);

        SlotRequest request = SlotRequest.builder()
                .eventId(1L)
                .startTime(ZonedDateTime.now(EVENT_ZONE).plusHours(1).toLocalDateTime())
                .build();

        when(timeZoneConverter.toUtc(any(LocalDateTime.class), anyString())).thenReturn(startTime);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(slotRepository.findByEventIdAndStartTime(request.getEventId(), startTime))
                .thenReturn(Optional.of(slot));
        when(slotRepository.countByEventAndBookedByEmailIsNotNullAndBookedByNameIsNotNull(event)).thenReturn(1);

        assertThrows(MaxCapacityExceededException.class, () -> slotService.bookSlot(request));

        verify(slotRepository).countByEventAndBookedByEmailIsNotNullAndBookedByNameIsNotNull(event);
    }

    @Test
    void shouldGetSlotsByEventIdSuccessfully() {
        Event event = new Event();
        event.setId(1L);

        Slot slot = new Slot();
        slot.setId(1L);

        when(slotRepository.findByEventId(event.getId())).thenReturn(List.of(slot));

        List<Slot> slots = slotService.getSlots(event.getId());

        assertNotNull(slots);
        assertEquals(1, slots.size());
        assertEquals(slot.getId(), slots.get(0).getId());

        verify(slotRepository).findByEventId(event.getId());
    }

    @Test
    void shouldReturnEmptyListWhenNoSlotsExistForEvent() {
        Event event = new Event();
        event.setId(1L);

        when(slotRepository.findByEventId(event.getId())).thenReturn(List.of());

        List<Slot> slots = slotService.getSlots(event.getId());

        assertNotNull(slots);
        assertEquals(0, slots.size());

        verify(slotRepository).findByEventId(event.getId());
    }

    @Test
    void shouldGetUserBookedSlotsSuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Slot slot = new Slot();
        slot.setId(1L);

        when(slotRepository.findByBookedByEmail(user.getEmail())).thenReturn(List.of(slot));

        List<Slot> slots = slotService.getBookedSlots(user);

        assertNotNull(slots);
        assertEquals(1, slots.size());
        assertEquals(slot.getId(), slots.get(0).getId());
        assertEquals(slot.getEvent(), slots.get(0).getEvent());
        assertEquals(slot.getStartTime(), slots.get(0).getStartTime());

        verify(slotRepository).findByBookedByEmail(user.getEmail());
    }

    @Test
    void shouldCancelSlotSuccessfully() {
        Slot slot = createTestSlot();
        slot.setBookedByEmail("test@example.com");
        slot.setBookedByName("Test User");

        OffsetDateTime slotStartTime = slot.getStartTime();

        CancelBookingRequest request = CancelBookingRequest.builder()
                .eventId(slot.getEvent().getId())
                .attendeeEmail(slot.getBookedByEmail())
                .startTime(ZonedDateTime.ofInstant(slotStartTime.toInstant(), EVENT_ZONE).toLocalDateTime())
                .build();

        when(timeZoneConverter.toUtc(any(LocalDateTime.class), anyString())).thenReturn(slotStartTime);
        when(eventRepository.findById(slot.getEvent().getId())).thenReturn(Optional.of(slot.getEvent()));
        when(slotRepository.findByEventIdAndStartTime(request.getEventId(), slotStartTime))
                .thenReturn(Optional.of(slot));
        when(slotRepository.save(any(Slot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Slot cancelledSlot = slotService.cancelBooking(request);

        assertNotNull(cancelledSlot);
        assertEquals(null, cancelledSlot.getBookedByEmail());
        assertEquals(null, cancelledSlot.getBookedByName());
        assertTrue(cancelledSlot.isAvailable());
        verify(eventRepository).findById(slot.getEvent().getId());
        verify(slotRepository).findByEventIdAndStartTime(request.getEventId(), slotStartTime);
        verify(slotRepository).save(any(Slot.class));
    }

    @Test
    void shouldThrowSlotNotFoundExceptionWhenCancellingNonExistentSlot() {
        LocalDateTime requestStartTime = ZonedDateTime.now(EVENT_ZONE).plusHours(1).toLocalDateTime();
        OffsetDateTime utcTime = ZonedDateTime.now(EVENT_ZONE).plusHours(1).toOffsetDateTime();

        CancelBookingRequest request = CancelBookingRequest.builder()
                .eventId(1L)
                .startTime(requestStartTime)
                .build();

        Event mockEvent = new Event();
        mockEvent.setId(1L);
        mockEvent.setTimeZone("Europe/Berlin");

        when(timeZoneConverter.toUtc(any(LocalDateTime.class), eq("Europe/Berlin"))).thenReturn(utcTime);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(mockEvent));
        when(slotRepository.findByEventIdAndStartTime(1L, utcTime))
                .thenReturn(Optional.empty());

        assertThrows(SlotNotFoundException.class, () -> slotService.cancelBooking(request));
    }

    @Test
    void shouldThrowInvalidSlotExceptionWhenCancellingPastSlot() {
        Slot slot = createTestSlot();
        slot.setStartTime(ZonedDateTime.now(EVENT_ZONE).minusHours(1).toOffsetDateTime());

        OffsetDateTime slotStartTime = slot.getStartTime();

        CancelBookingRequest request = CancelBookingRequest.builder()
                .eventId(slot.getEvent().getId())
                .attendeeEmail(slot.getBookedByEmail())
                .startTime(ZonedDateTime.ofInstant(slotStartTime.toInstant(), EVENT_ZONE).toLocalDateTime())
                .build();

        when(timeZoneConverter.toUtc(any(LocalDateTime.class), anyString())).thenReturn(slotStartTime);
        when(eventRepository.findById(slot.getEvent().getId())).thenReturn(Optional.of(slot.getEvent()));
        when(slotRepository.findByEventIdAndStartTime(request.getEventId(), slotStartTime))
                .thenReturn(Optional.of(slot));

        assertThrows(InvalidSlotException.class, () -> slotService.cancelBooking(request));
    }

    @Test
    void shouldThrowInvalidSlotExceptionWhenCancellingUnbookedSlot() {
        Slot slot = createTestSlot();
        slot.setBookedByEmail(null);
        slot.setBookedByName(null);

        OffsetDateTime slotStartTime = slot.getStartTime();

        CancelBookingRequest request = CancelBookingRequest.builder()
                .eventId(slot.getEvent().getId())
                .attendeeEmail("test@example.com")
                .startTime(ZonedDateTime.ofInstant(slotStartTime.toInstant(), EVENT_ZONE).toLocalDateTime())
                .build();

        when(timeZoneConverter.toUtc(any(LocalDateTime.class), anyString())).thenReturn(slotStartTime);
        when(eventRepository.findById(slot.getEvent().getId())).thenReturn(Optional.of(slot.getEvent()));
        when(slotRepository.findByEventIdAndStartTime(request.getEventId(), slotStartTime))
                .thenReturn(Optional.of(slot));

        assertThrows(InvalidSlotException.class, () -> slotService.cancelBooking(request));
    }

    @Test
    void shouldThrowUnauthorizedAccessExceptionWhenCancellingWithWrongEmail() {
        Slot slot = createTestSlot();
        slot.setBookedByEmail("user@example.com");

        OffsetDateTime slotStartTime = slot.getStartTime();

        CancelBookingRequest request = CancelBookingRequest.builder()
                .eventId(slot.getEvent().getId())
                .attendeeEmail("other@example.com")
                .startTime(ZonedDateTime.ofInstant(slotStartTime.toInstant(), EVENT_ZONE).toLocalDateTime())
                .build();

        when(timeZoneConverter.toUtc(any(LocalDateTime.class), anyString())).thenReturn(slotStartTime);
        when(eventRepository.findById(slot.getEvent().getId())).thenReturn(Optional.of(slot.getEvent()));
        when(slotRepository.findByEventIdAndStartTime(request.getEventId(), slotStartTime))
                .thenReturn(Optional.of(slot));

        assertThrows(UnauthorizedAccessException.class, () -> slotService.cancelBooking(request));
    }

    @Test
    void shouldThrowInvalidSlotExceptionWhenCancellationsNotAllowed() {
        AvailabilityRules rules = new AvailabilityRules();
        rules.setAllowsCancellations(false);

        Event event = new Event();
        event.setId(1L);
        event.setAvailabilityRules(rules);
        event.setTimeZone("Europe/Berlin");

        Slot slot = createTestSlot();
        slot.setEvent(event);
        slot.setBookedByEmail("test@example.com");
        slot.setBookedByName("Test User");

        OffsetDateTime slotStartTime = slot.getStartTime();

        CancelBookingRequest request = CancelBookingRequest.builder()
                .eventId(slot.getEvent().getId())
                .attendeeEmail(slot.getBookedByEmail())
                .startTime(ZonedDateTime.ofInstant(slotStartTime.toInstant(), EVENT_ZONE).toLocalDateTime())
                .build();

        when(timeZoneConverter.toUtc(any(LocalDateTime.class), eq("Europe/Berlin"))).thenReturn(slotStartTime);
        when(eventRepository.findById(slot.getEvent().getId())).thenReturn(Optional.of(event));
        when(slotRepository.findByEventIdAndStartTime(request.getEventId(), slotStartTime))
                .thenReturn(Optional.of(slot));

        assertThrows(InvalidSlotException.class, () -> slotService.cancelBooking(request));
    }

    @Test
    void shouldGetAvailableSlotsByShareableIdSuccessfully() {
        AvailabilityRules rules = new AvailabilityRules();
        rules.setPublic(true);

        Event event = new Event();
        event.setId(1L);
        event.setShareableId("event1");
        event.setAvailabilityRules(rules);

        OffsetDateTime startTime = ZonedDateTime.now(EVENT_ZONE).plusHours(1).toOffsetDateTime();
        Slot slot = new Slot();
        slot.setId(1L);
        slot.setEvent(event);
        slot.setStartTime(startTime);
        slot.setBookedByEmail(null);
        slot.setBookedByName(null);

        when(eventRepository.findByShareableId(event.getShareableId())).thenReturn(Optional.of(event));
        when(slotRepository.findByEventAndBookedByEmailIsNullAndBookedByNameIsNull(event))
                .thenReturn(List.of(slot));

        List<Slot> slots = slotService.getAvailableSlotsByShareableId(event.getShareableId());

        assertNotNull(slots);
        assertEquals(1, slots.size());
        assertEquals(slot.getId(), slots.get(0).getId());
        assertEquals(slot.getEvent(), slots.get(0).getEvent());
        assertEquals(slot.getStartTime(), slots.get(0).getStartTime());

        verify(eventRepository).findByShareableId(event.getShareableId());
        verify(slotRepository).findByEventAndBookedByEmailIsNullAndBookedByNameIsNull(event);
    }

    @Test
    void shouldThrowUnauthorizedAccessExceptionWhenEventIsPrivate() {
        AvailabilityRules rules = new AvailabilityRules();
        rules.setPublic(false);

        Event event = new Event();
        event.setId(1L);
        event.setShareableId("event1");
        event.setAvailabilityRules(rules);

        when(eventRepository.findByShareableId(event.getShareableId())).thenReturn(Optional.of(event));

        assertThrows(UnauthorizedAccessException.class,
                () -> slotService.getAvailableSlotsByShareableId(event.getShareableId()));
    }

    @Test
    void shouldThrowEventNotFoundException() {
        when(eventRepository.findByShareableId(anyString())).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> slotService.getAvailableSlotsByShareableId(anyString()));

        verify(slotRepository, never()).countByEventAndBookedByEmailIsNotNullAndBookedByNameIsNotNull(any(Event.class));
    }

    @Test
    void shouldGetSlotByIdSuccessfully() {
        Slot testSlot = createTestSlot();
        when(slotRepository.findById(anyLong())).thenReturn(Optional.of(testSlot));

        Slot slot = slotService.getSlotById(1L);

        assertNotNull(slot);
        assertEquals(testSlot.getId(), slot.getId());
        assertEquals(testSlot.getEvent(), slot.getEvent());
        assertEquals(testSlot.getStartTime(), slot.getStartTime());
        assertEquals(testSlot.getEndTime(), slot.getEndTime());
    }

    @Test
    void shouldThrowSlotNotFoundExceptionWhenSlotNotFoundById() {
        when(slotRepository.findById(anyLong())).thenReturn(Optional.empty());

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
        rules.setPublic(true);

        Event event = new Event();
        event.setId(1L);
        event.setEventName("Event 1");
        event.setHost(mockUser);
        event.setAvailabilityRules(rules);
        event.setTimeZone("Europe/Berlin");

        Slot slot = new Slot();
        slot.setId(1L);
        slot.setEvent(event);
        slot.setStartTime(ZonedDateTime.now(EVENT_ZONE).plusHours(1).toOffsetDateTime());
        slot.setEndTime(ZonedDateTime.now(EVENT_ZONE).plusHours(2).toOffsetDateTime());

        return slot;
    }
}
