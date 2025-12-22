package com.example.SlotlyV2.service;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.example.SlotlyV2.dto.SlotRequest;
import com.example.SlotlyV2.exception.SlotNotFoundException;
import com.example.SlotlyV2.model.Event;
import com.example.SlotlyV2.model.Slot;
import com.example.SlotlyV2.model.User;
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
        List<Slot> slots = new ArrayList<>();

        while (start.plusMinutes(event.getRules().getSlotDurationMinutes()).isBefore(end)) {
            Slot slot = new Slot();
            slot.setEvent(event);
            slot.setStartTime(start);
            slot.setEndTime(start.plusMinutes(event.getRules().getSlotDurationMinutes()));
            slot.setBookedBy(null);

            slots.add(slot);
            start = start.plusMinutes(event.getRules().getSlotDurationMinutes());
        }

        slotRepository.saveAll(slots);
    }

    public List<Slot> getSlots(Long eventId) {
        return slotRepository.findByEventId(eventId);
    }

    public Slot bookSlot(SlotRequest request) {
        Slot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new SlotNotFoundException("Slot Not Found with Id " + request.getSlotId()));

        slot.setBookedBy(request.getAttendeeName());
        slot.setBookedAt(LocalDateTime.now());

        return slot;
    }

    public List<Slot> getBookedSlots(User user) {
        return slotRepository.findBookedBy(user);
    }
}
