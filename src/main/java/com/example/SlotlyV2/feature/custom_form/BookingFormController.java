package com.example.SlotlyV2.feature.custom_form;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.feature.custom_form.dto.BookingFormRequest;
import com.example.SlotlyV2.feature.custom_form.dto.BookingFormResponse;
import com.example.SlotlyV2.feature.custom_form.dto.FormFieldDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/booking-form")
@RequiredArgsConstructor
public class BookingFormController {
    private final BookingFormService bookingFormService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BookingFormResponse> createBookingForm(@Valid @RequestBody BookingFormRequest request) {
        BookingForm bookingForm = bookingFormService.createBookingForm(request);
        return new ApiResponse<>("Booking form created successfully", new BookingFormResponse(bookingForm));
    }

    @GetMapping
    public ApiResponse<List<FormFieldDTO>> getBookingForm(@Valid @RequestBody BookingFormRequest request) {
        List<FormField> formFields = bookingFormService.getBookingForm(request);
        return new ApiResponse<>("Booking form fetched successfully",
                formFields.stream().map(formField -> new FormFieldDTO(formField)).collect(Collectors.toList()));
    }
}
