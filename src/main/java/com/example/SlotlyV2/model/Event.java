package com.example.SlotlyV2.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "event_name")
    private String eventName;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    private LocalDateTime createdAt;

    @Embedded
    private AvailabilityRules rules;

    @Column(unique = true)
    private String shareableId;

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
        return UUID.randomUUID().toString();
    }
}
