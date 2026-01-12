package com.example.SlotlyV2.feature.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.SlotlyV2.feature.availability.AvailabilityRules;
import com.example.SlotlyV2.feature.event.enums.RecurrenceFrequency;
import com.example.SlotlyV2.feature.event.enums.RecurringEndType;
import com.example.SlotlyV2.feature.slot.Slot;
import com.example.SlotlyV2.feature.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
@EntityListeners(AuditingEntityListener.class)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_name")
    private String eventName;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "event_start")
    private LocalDateTime eventStart;

    @Column(name = "event_end")
    private LocalDateTime eventEnd;

    @Column(name = "time_zone")
    private String timeZone;

    @Embedded
    private AvailabilityRules rules;

    @Column(unique = true)
    private String shareableId;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Slot> slots = new ArrayList<>();

    @Column(name = "isRecurring")
    private boolean isRecurring = false;

    @Column(name = "recurrence_frequency")
    @Enumerated(EnumType.STRING)
    private RecurrenceFrequency recurrenceFrequency;

    @Column(name = "recurrence_interval")
    private Integer recurrenceInterval = 1;

    @Column(name = "recurrence_days_of_week")
    private Integer[] recurrenceDaysOfWeek;

    @Column(name = "recurrent_end_type")
    @Enumerated(EnumType.STRING)
    private RecurringEndType recurrentEndType;

    @Column(name = "recurrence_occurrences")
    private Integer recurrenceOccurrences;

    @Column(name = "recurrence_end_date")
    private LocalDateTime recurrenceEndDate;

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();

        if (shareableId == null) {
            shareableId = generateShareableId();
        }

        if (rules == null) {
            rules = new AvailabilityRules();
        }
    }

    private String generateShareableId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
