import type { FieldStatus } from "@/features/bookings/types/Booking";

export interface BookingFormRequest {
    fields: BookingFormFieldRequest[];
}

export interface BookingFormResponse {
    fields: BookingFormFieldResponse[];
}

export interface BookingFormAnswerRequest {
    fieldId: string;
    fieldResponse: string;
}

export interface BookingFormAnswerResponse {
    fieldLabel: string;
    fieldResponse: string;
}

export interface BookingFormFieldRequest {
    label: string;
    fieldType: FieldStatus;
    required: boolean;
    displayOrder: number;
}

export interface BookingFormFieldResponse {
    id: string;
    label: string;
    fieldType: FieldStatus;
    required: boolean;
    displayOrder: number;
}

export interface BookingFormSubmissionRequest {
    answers: BookingFormAnswerRequest[];
}
