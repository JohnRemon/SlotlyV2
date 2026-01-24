package com.example.SlotlyV2.feature.calendar;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.SlotlyV2.feature.calendar.enums.SyncStatus;
import com.example.SlotlyV2.feature.slot.Slot;

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
import lombok.Data;

@Entity
@Table(name = "slot_google_events", uniqueConstraints = @UniqueConstraint(columnNames = { "slot_id" }))
@Data
public class SlotGoogleEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    @Column(nullable = false)
    private String googleEventId;

    @Column(nullable = false)
    private OffsetDateTime syncedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncStatus syncStatus;

    @Column
    private String error;

    @Column
    private Integer retryCount = 0;

    public void markSynced(String eventId) {
        this.googleEventId = eventId;
        this.syncStatus = SyncStatus.SYNCED;
        this.syncedAt = OffsetDateTime.now();
        this.error = null;
        this.retryCount = 0;
    }

    public void markFailed(String error) {
        this.syncStatus = SyncStatus.FAILED;
        this.syncedAt = OffsetDateTime.now();
        this.error = error;
        this.retryCount++;
    }
}
