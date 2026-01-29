package com.example.SlotlyV2.feature.recurrence;

import java.time.OffsetDateTime;

import com.example.SlotlyV2.feature.event.enums.RecurrenceFrequency;
import com.example.SlotlyV2.feature.event.enums.RecurrenceEndType;

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
public class RecurrenceRules {
    @Column(name = "recurrence_frequency")
    @Enumerated(EnumType.STRING)
    private RecurrenceFrequency recurrenceFrequency;

    @Column(name = "recurrence_day_of_week")
    private Integer recurrenceDayOfWeek;

    @Column(name = "recurrent_end_type")
    @Enumerated(EnumType.STRING)
    private RecurrenceEndType recurrenceEndType;

    @Column(name = "recurrence_occurrences")
    private Integer recurrenceOccurrences;

    @Column(name = "recurrence_end_date")
    private OffsetDateTime recurrenceEndDate;
}

// TODO: Add Interval
