import type { BookingFormSubmissionRequest } from "@/features/booking-page/types/BookingForms";

export type BookingStatus = "CONFIRMED" | "CANCELLED" | "NO_SHOW" | "PAST";
export type BookingTab = "CONFIRMED" | "CANCELLED" | "NO_SHOW" | "PAST";
export type FieldStatus = "TEXT" | "PHONE";

export interface Booking {
    id: number;
    attendeeName: string;
    attendeeEmail: string;
    eventName: string;
    startTime: string;
    endTime: string;
    bookingStatus: BookingStatus;
    notes?: string;
    cancellationReason?: string;
    cancelledAt?: string;
    formAnswers?: {
        fieldLabel: string;
        fieldAnswer: string;
    }[];
    createdAt?: string;
}

export interface CreateBookingRequest {
    eventId: number;
    slotId: number;
    attendeeName: string;
    attendeeEmail: string;
    notes?: string;
    formSubmission?: BookingFormSubmissionRequest;
}

export interface CancelBookingRequest {
    bookingId: number;
    attendeeEmail: string;
    cancellationReason: string;
}
