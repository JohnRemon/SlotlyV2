package com.example.SlotlyV2.feature.event;

import java.time.LocalDateTime;

import com.example.SlotlyV2.feature.event.enums.RecurrenceFrequency;
import com.example.SlotlyV2.feature.event.enums.RecurringEndType;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringRules {
    @Column(name = "recurrence_frequency")
    @Enumerated(EnumType.STRING)
    private RecurrenceFrequency recurrenceFrequency;

    @Column(name = "recurrence_day_of_week")
    private Integer recurrenceDayOfWeek;

    @Column(name = "recurrent_end_type")
    @Enumerated(EnumType.STRING)
    private RecurringEndType recurrentEndType;

    @Column(name = "recurrence_occurrences")
    private Integer recurrenceOccurrences;

    @Column(name = "recurrence_end_date")
    private LocalDateTime recurrenceEndDate;
}
