export interface PublicEvent {
    id: number;
    eventName: string;
    description?: string;
    eventStart: string;
    eventEnd: string;
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
