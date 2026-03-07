package com.example.SlotlyV2.feature.schedule;

import java.util.List;
import java.util.UUID;

import com.example.SlotlyV2.feature.user.User;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ElementCollection
    @CollectionTable(name = "schedule_daily_schedules", joinColumns = @JoinColumn(name = "schedule_id"))
    private List<DailySchedule> dailySchedules;

    // TODO: add a default schedule and use this shcedule when creating events
    @Builder.Default
    private Boolean isDefault = false;
}
