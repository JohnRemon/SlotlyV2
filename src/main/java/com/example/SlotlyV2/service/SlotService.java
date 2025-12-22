package com.example.SlotlyV2.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.SlotlyV2.model.Event;
import com.example.SlotlyV2.model.Slot;
import com.example.SlotlyV2.repository.SlotRepository;

@Service
public class SlotService {
    private final SlotRepository slotRepository;

    public SlotService(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    public void generateSlots(Event event) {
        LocalDateTime start = event.getEventStart();
        LocalDateTime end = event.getEventEnd();

        while (start.isBefore(end)) {
            Slot slot = new Slot();
            slot.setEvent(event);
            slot.setStartTime(start);
            slot.setEndTime(start.plusMinutes(event.getRules().getSlotDurationMinutes()));
            slot.setBookedBy(null);

            slotRepository.save(slot);

            start = start.plusMinutes(event.getRules().getSlotDurationMinutes());
        }
    }
}
