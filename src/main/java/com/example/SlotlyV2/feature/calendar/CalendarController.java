package com.example.SlotlyV2.feature.calendar;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.exception.slot.SlotNotBookedException;
import com.example.SlotlyV2.feature.slot.Slot;
import com.example.SlotlyV2.feature.slot.SlotService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/calendar")
public class CalendarController {
    private final CalendarService calendarService;
    private final SlotService slotService;

    @GetMapping("/{slotId}")
    public ResponseEntity<String> downloadCalendar(@PathVariable Long slotId) {
        Slot slot = slotService.getSlotById(slotId);

        if (slot.isAvailable()) {
            throw new SlotNotBookedException("Cannot generate calendar for unbooked slot");
        }

        String ics = calendarService.generateIcsFile(slot);

        return ResponseEntity.ok()
                .header("Content-Type", "text/calendar; charset=utf-8")
                .header("Content-Disposition", "attachment; filename=event.ics")
                .body(ics);
    }
}
