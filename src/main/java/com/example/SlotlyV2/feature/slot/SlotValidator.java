package com.example.SlotlyV2.feature.slot;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.common.exception.event.MaxCapacityExceededException;
import com.example.SlotlyV2.common.exception.slot.InvalidSlotException;
import com.example.SlotlyV2.common.exception.slot.SlotAlreadyBookedException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SlotValidator {
    private final SlotRepository slotRepository;

    public void validateSlotForBooking(Slot slot) {
        validateSlotIsAvailable(slot);
        validateSlotIsNotInPast(slot);
        validateEventMaxCapacity(slot);
    }

    public void validateSlotForCancellation(Slot slot, String attendeeEmail) {
        validateSlotIsNotInPast(slot);
        validateSlotIsBooked(slot);
        validateCancellationsAllowed(slot);
        validateAttendeeEmail(slot, attendeeEmail);
    }

    private void validateEventMaxCapacity(Slot slot) {
        Integer maxCapacity = slot.getEvent().getAvailabilityRules().getMaxCapacity();
        if (maxCapacity != null) {
            Integer currentCapacity = slotRepository
                    .countByEventAndBookedByEmailIsNotNullAndBookedByNameIsNotNull(slot.getEvent());
            if (currentCapacity >= maxCapacity) {
                throw new MaxCapacityExceededException("This event has reached maximum capacity");
            }
        }
    }

    private void validateSlotIsNotInPast(Slot slot) {
        ZoneId zone = ZoneId.of(slot.getEvent().getTimeZone());
        if (slot.getStartTime().toZonedDateTime().isBefore(ZonedDateTime.now(zone))) {
            throw new InvalidSlotException("This slot is in the past");
        }
    }

    private void validateSlotIsAvailable(Slot slot) {
        if (!slot.isAvailable()) {
            throw new SlotAlreadyBookedException("This slot is already booked. Please choose another slot");
        }
    }

    private void validateCancellationsAllowed(Slot slot) {
        if (!slot.getEvent().getAvailabilityRules().isAllowsCancellations()) {
            throw new InvalidSlotException("Cancellations are not allowed for this event");
        }
    }

    private void validateAttendeeEmail(Slot slot, String attendeeEmail) {
        if (!slot.getBookedByEmail().equals(attendeeEmail)) {
            throw new UnauthorizedAccessException("This email is not associated with the booked slot");
        }
    }

    private void validateSlotIsBooked(Slot slot) {
        if (slot.isAvailable()) {
            throw new InvalidSlotException("This slot is not booked");
        }
    }

}
