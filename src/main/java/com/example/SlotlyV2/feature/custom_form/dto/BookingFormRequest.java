package com.example.SlotlyV2.feature.custom_form.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFormRequest {

    @NotNull(message = "Event ID is required")
    private Long eventId;

    @Valid
    private List<FormFieldDTO> formFields;
}
