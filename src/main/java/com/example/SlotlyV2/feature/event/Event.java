package com.example.SlotlyV2.feature.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.SlotlyV2.feature.availability.AvailabilityRules;
import com.example.SlotlyV2.feature.slot.Slot;
import com.example.SlotlyV2.feature.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
        return "event-" + System.currentTimeMillis() + "-" +
                eventName.toLowerCase()
                        .replaceAll("[^a-z0-9]+", "-")
                        .replaceAll("^-|-$", "");
    }
}
