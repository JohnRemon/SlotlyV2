package com.example.SlotlyV2.feature.calendar;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.SlotlyV2.feature.booking.Booking;
import com.example.SlotlyV2.feature.calendar.enums.SyncStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "slot_google_events", uniqueConstraints = @UniqueConstraint(columnNames = { "slot_id" }))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BookingGoogleEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private String googleEventId;

    @Column(nullable = false)
    private OffsetDateTime syncedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncStatus syncStatus;

    public void markSynced(String eventId) {
        this.googleEventId = eventId;
        this.syncStatus = SyncStatus.SYNCED;
        this.syncedAt = OffsetDateTime.now();
    }

    public void markFailed(String error) {
        this.syncStatus = SyncStatus.FAILED;
        this.syncedAt = OffsetDateTime.now();
    }
}
