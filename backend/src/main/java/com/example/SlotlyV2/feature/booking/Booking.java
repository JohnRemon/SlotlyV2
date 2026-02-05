package com.example.SlotlyV2.feature.booking;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.SlotlyV2.common.exception.slot.SlotNotBookedException;
import com.example.SlotlyV2.feature.booking_form.FieldAnswer;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.slot.Slot;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false, unique = true)
    private Slot slot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "attendee_name", nullable = false)
    private String attendeeName;

    @Column(name = "attendee_email", nullable = false)
    private String attendeeEmail;

    @Column(name = "notes", length = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.CONFIRMED;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FieldAnswer> formAnswers = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "cancelled_at")
    private OffsetDateTime cancelledAt;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        if (status == null) {
            status = BookingStatus.CONFIRMED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public boolean isActive() {
        return status == BookingStatus.CONFIRMED;
    }

    public boolean canBeCancelled() {
        return status == BookingStatus.CONFIRMED;
    }

    public void cancel(String reason) {
        if (!canBeCancelled()) {
            throw new SlotNotBookedException("Booking cannot be cancelled in status: " + status);
        }

        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = OffsetDateTime.now();
        this.cancellationReason = reason;
    }

    public void markAsNoShow() {
        if (status != BookingStatus.CONFIRMED) {
            throw new SlotNotBookedException("Only confirmed bookings can be marked as no-show");
        }

        this.status = BookingStatus.NO_SHOW;
        this.updatedAt = OffsetDateTime.now();
    }

    public void addFormAnswer(FieldAnswer fieldAnswer) {
        formAnswers.add(fieldAnswer);
        fieldAnswer.setBooking(this);
    }

    public void removeFormAnswer(FieldAnswer formAnswer) {
        formAnswers.remove(formAnswer);
        formAnswer.setBooking(null);
    }

    public String getAttendeeDisplayName() {
        return attendeeName != null && !attendeeName.isBlank()
                ? attendeeName
                : attendeeEmail;
    }
}
