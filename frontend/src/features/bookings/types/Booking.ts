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

export interface PublicEvent {
    id: number;
    eventName: string;
    description?: string;
    eventStart: string;
    eventEnd: string;
    timeZone: string;
    shareableId: string;
    host: {
        firstName: string;
        lastName: string;
    };
    availabilityRulesDTO: {
        slotDurationMinutes: number;
    };
    bookingForm?: {
        fields: FormField[];
    };
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

export interface BookingFormRequest {
    fields: BookingFormFieldRequest[];
}

export interface CreateBookingRequest {
    eventId: number;
    slotId: number;
    attendeeName: string;
    attendeeEmail: string;
    notes?: string;
    formSubmission?: BookingFormSubmissionRequest;
}

export interface FormField {
    id: string;
    label: string;
    fieldType: "TEXT" | "TEXTAREA" | "NUMBER";
    required: boolean;
    displayOrder: number;
}

export interface Slot {
    id: number;
    startTime: string;
    endTime: string;
}

export interface BookingPayload {
    slotId: number;
    eventId: number;
    attendeeName: string;
    attendeeEmail: string;
    notes?: string;
    formSubmission?: {
        answers: { fieldId: string; fieldResponse: string }[];
    };
}
