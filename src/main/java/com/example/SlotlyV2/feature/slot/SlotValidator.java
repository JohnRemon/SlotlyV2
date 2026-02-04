package com.example.SlotlyV2.feature.slot;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.common.exception.event.InvalidBookingException;
import com.example.SlotlyV2.common.exception.event.MaxCapacityExceededException;
import com.example.SlotlyV2.common.exception.slot.InvalidSlotException;
import com.example.SlotlyV2.common.exception.slot.SlotAlreadyBookedException;
import com.example.SlotlyV2.feature.booking.Booking;
import com.example.SlotlyV2.feature.booking.BookingRepository;
import com.example.SlotlyV2.feature.booking.BookingStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SlotValidator {
    private final BookingRepository bookingRepository;

    public void validateSlotForBooking(Slot slot) {
        validateSlotIsAvailable(slot);
        validateSlotIsNotInPast(slot);
        validateEventMaxCapacity(slot);
        validateMinimumNoticeHours(slot);
        validateMaximumAdvanceDays(slot);
    }

    public void validateSlotForCancellation(Booking booking, String attendeeEmail) {
        validateSlotIsNotInPast(booking.getSlot());
        validateSlotIsBooked(booking.getSlot());
        validateCancellationsAllowed(booking.getSlot());
        validateAttendeeEmail(booking, attendeeEmail);
    }

    private void validateEventMaxCapacity(Slot slot) {
        Integer maxCapacity = slot.getEvent().getAvailabilityRules().getMaxCapacity();
        if (maxCapacity != null) {
            Integer currentCapacity = bookingRepository.countByEventAndStatus(slot.getEvent(), BookingStatus.CONFIRMED);
            if (currentCapacity >= maxCapacity) {
                throw new MaxCapacityExceededException("This event has reached maximum capacity");
            }
        }
    }

    private void validateSlotIsNotInPast(Slot slot) {
        if (slot.getStartTime().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            throw new InvalidSlotException("This slot is in the past");
        }
    }

    private void validateSlotIsAvailable(Slot slot) {
        if (!slot.isAvailable()) {
            throw new SlotAlreadyBookedException("This slot is already booked. Please choose another slot");
        }
    }

    private void validateCancellationsAllowed(Slot slot) {
        if (!slot.getEvent().getAvailabilityRules().getAllowsCancellations()) {
            throw new InvalidSlotException("Cancellations are not allowed for this event");
        }
    }

    private void validateAttendeeEmail(Booking booking, String attendeeEmail) {
        if (!booking.getAttendeeEmail().equals(attendeeEmail)) {
            throw new UnauthorizedAccessException("This email is not associated with the booked slot");
        }
    }

    private void validateSlotIsBooked(Slot slot) {
        if (slot.isAvailable()) {
            throw new InvalidSlotException("This slot is not booked");
        }
    }

    private void validateMinimumNoticeHours(Slot slot) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime startTime = slot.getStartTime();
        Integer minimumNoticeHours = slot.getEvent().getAvailabilityRules().getMinimumNoticeHours();
        if (startTime.isBefore(now.plusHours(minimumNoticeHours))) {
            throw new InvalidBookingException("Bookings require at least " + minimumNoticeHours + " hours notice");
        }
    }

    private void validateMaximumAdvanceDays(Slot slot) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime startTime = slot.getStartTime();
        Integer maximumAdvanceDays = slot.getEvent().getAvailabilityRules().getMaximumAdvanceDays();
        if (startTime.isAfter(now.plusDays(maximumAdvanceDays))) {
            throw new InvalidBookingException(
                    "Bookings can't be made more than " + maximumAdvanceDays + " days in advance");
        }
    }
}
