package com.example.SlotlyV2.feature.schedule;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSchedule {
    private boolean[] daysOfWeek;
    private LocalTime[] startTimes;
    private LocalTime[] endTimes;
}
