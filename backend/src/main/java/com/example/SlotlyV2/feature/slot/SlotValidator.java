// -- SlotController.java -------------------------------------------------------
package com.example.SlotlyV2.feature.slot;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.common.exception.event.InvalidBookingException;
import com.example.SlotlyV2.common.exception.event.MaxCapacityExceededException;
import com.example.SlotlyV2.common.exception.slot.InvalidSlotException;
import com.example.SlotlyV2.common.exception.slot.SlotAlreadyBookedException;
import com.example.SlotlyV2.common.exception.slot.SlotNotBookedException;
import com.example.SlotlyV2.feature.booking.Booking;
import com.example.SlotlyV2.feature.booking.BookingRepository;
import com.example.SlotlyV2.feature.booking.BookingStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SlotValidator {

    private final BookingRepository bookingRepository;

    public void validateSlotForBooking(Slot slot) {
        validateNotInPast(slot);
        validateIsAvailable(slot);
        validateMaxCapacity(slot);
        validateMinimumNotice(slot);
        validateMaximumAdvance(slot);
    }

    public void validateSlotForCancellation(Booking booking, String attendeeEmail) {
        validateNotInPast(booking.getSlot());
        validateIsBooked(booking.getSlot());
        validateCancellationsAllowed(booking.getSlot());
        validateAttendeeEmail(booking, attendeeEmail);
    }

    // -- Private validators ----------------------------------------------------

    private void validateNotInPast(Slot slot) {
        if (slot.getStartTime().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            throw new InvalidSlotException("This slot is in the past");
        }
    }

    private void validateIsAvailable(Slot slot) {
        if (!slot.isAvailable()) {
            throw new SlotAlreadyBookedException("This slot is already booked. Please choose another slot");
        }
    }

    private void validateIsBooked(Slot slot) {
        if (slot.isAvailable()) {
            throw new SlotNotBookedException("This slot is not booked");
        }
    }

    private void validateMaxCapacity(Slot slot) {
        Integer maxCapacity = slot.getEvent().getAvailabilityRules().getMaxCapacity();
        if (maxCapacity == null)
            return;

        int current = bookingRepository.countByEventAndStatus(slot.getEvent(), BookingStatus.CONFIRMED);
        if (current >= maxCapacity) {
            throw new MaxCapacityExceededException("This event has reached maximum capacity");
        }
    }

    private void validateCancellationsAllowed(Slot slot) {
        if (!slot.getEvent().getAvailabilityRules().getAllowsCancellations()) {
            throw new InvalidSlotException("Cancellations are not allowed for this event");
        }
    }

    private void validateAttendeeEmail(Booking booking, String attendeeEmail) {
        if (!booking.getAttendeeEmail().equals(attendeeEmail)) {
            throw new UnauthorizedAccessException("This email is not associated with this booking");
        }
    }

    private void validateMinimumNotice(Slot slot) {
        int minimumNoticeHours = slot.getEvent().getAvailabilityRules().getMinimumNoticeHours();
        if (minimumNoticeHours == 0)
            return;

        OffsetDateTime earliest = OffsetDateTime.now(ZoneOffset.UTC).plusHours(minimumNoticeHours);
        if (slot.getStartTime().isBefore(earliest)) {
            throw new InvalidBookingException(
                    "Bookings require at least " + minimumNoticeHours + " hours notice");
        }
    }

    private void validateMaximumAdvance(Slot slot) {
        int maximumAdvanceDays = slot.getEvent().getAvailabilityRules().getMaximumAdvanceDays();
        OffsetDateTime latest = OffsetDateTime.now(ZoneOffset.UTC).plusDays(maximumAdvanceDays);
        if (slot.getStartTime().isAfter(latest)) {
            throw new InvalidBookingException(
                    "Bookings cannot be made more than " + maximumAdvanceDays + " days in advance");
        }
    }
}
