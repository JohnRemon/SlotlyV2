package com.example.SlotlyV2.feature.event;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.SlotlyV2.feature.availability.AvailabilityRules;
import com.example.SlotlyV2.feature.booking_form.BookingForm;
import com.example.SlotlyV2.feature.recurrence.RecurrenceRules;
import com.example.SlotlyV2.feature.slot.Slot;
import com.example.SlotlyV2.feature.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Column(name = "event_start")
    private OffsetDateTime eventStart;

    @Column(name = "event_end")
    private OffsetDateTime eventEnd;

    @Column(unique = true)
    private String shareableId;

    @Column(name = "isRecurring")
    @Builder.Default
    private boolean isRecurring = false;

    @Embedded
    private AvailabilityRules availabilityRules;

    @Embedded
    private RecurrenceRules recurrenceRules;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Slot> slots = new ArrayList<>();

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private BookingForm bookingForm;

    @PrePersist
    private void onCreate() {
        if (shareableId == null) {
            shareableId = generateShareableId();
        }

        if (availabilityRules == null) {
            availabilityRules = new AvailabilityRules();
        }
    }

    private String generateShareableId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void markDeleted(OffsetDateTime timestamp) {
        this.deletedAt = timestamp;
    }
}
