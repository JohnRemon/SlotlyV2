package com.example.SlotlyV2.feature.event.dto;

import java.time.OffsetDateTime;

import com.example.SlotlyV2.feature.availability.dto.AvailabilityRulesDTO;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormRequest;
import com.example.SlotlyV2.feature.recurrence.dto.RecurrenceRulesDTO;
import com.example.SlotlyV2.feature.schedule.dto.ScheduleRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {
    @NotBlank(message = "Event name is required")
    private String eventName;

    private String description;

    @NotNull(message = "Event start time is required")
    @Future(message = "Event must be in the future")
    private OffsetDateTime eventStart;

    @NotNull(message = "Event end time is required")
    @Future(message = "Event must be in the future")
    private OffsetDateTime eventEnd;

    @Valid
    private AvailabilityRulesDTO availabilityRulesDTO;

    @Valid
    private RecurrenceRulesDTO recurrenceRulesDTO;

    @Valid
    private BookingFormRequest bookingForm;

    @Valid
    private ScheduleRequest schedule;
}
