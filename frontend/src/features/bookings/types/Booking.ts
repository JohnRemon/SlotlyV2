export type BookingStatus = "CONFIRMED" | "CANCELLED" | "NO_SHOW" | "PAST";

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
    createdAt: string;
}

export type BookingTab = "CONFIRMED" | "CANCELLED" | "NO_SHOW" | "PAST";
