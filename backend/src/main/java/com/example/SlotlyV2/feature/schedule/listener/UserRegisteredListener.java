package com.example.SlotlyV2.feature.schedule.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.SlotlyV2.feature.schedule.ScheduleService;
import com.example.SlotlyV2.feature.schedule.event.UserRegisteredEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRegisteredListener {
    private final ScheduleService scheduleService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onUserRegistered(UserRegisteredEvent event) {
        scheduleService.createDefaultScheduleForUser(event.user());
    }
}
