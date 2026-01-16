package com.example.SlotlyV2.feature.schedule;

import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BlockedPeriod {
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private boolean isRecurring;
}
