import type { FieldStatus } from "@/features/bookings/types/Booking";

export interface FormField {
    id: string;
    label: string;
    fieldType: "TEXT" | "TEXTAREA" | "NUMBER";
    required: boolean;
    displayOrder: number;
}

export interface BookingFormAnswerRequest {
    fieldId: string;
    fieldResponse: string;
}

export interface BookingFormSubmissionRequest {
    answers: BookingFormAnswerRequest[];
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

export interface BookingFormRequest {
    fields: BookingFormFieldRequest[];
}

export interface BookingFormResponse {
    fields: BookingFormFieldResponse[];
}
