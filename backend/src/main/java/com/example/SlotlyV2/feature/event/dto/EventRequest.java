package com.example.SlotlyV2.feature.event.dto;

import com.example.SlotlyV2.feature.availability.dto.AvailabilityRulesDTO;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormRequest;
import com.example.SlotlyV2.feature.schedule.dto.ScheduleRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

    @Valid
    private AvailabilityRulesDTO availabilityRules;

    @Valid
    private BookingFormRequest bookingForm;

    @Valid
    private ScheduleRequest schedule;
}
