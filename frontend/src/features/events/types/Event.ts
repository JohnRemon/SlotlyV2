import type {
    BookingFormResponse,
    FormField,
    BookingFormRequest,
} from "@/features/booking-page/types/BookingForms";

export type RecurrenceFrequency = "DAILY" | "WEEKLY" | "MONTHLY" | "CUSTOM";
export type RecurrenceEndType = "NEVER" | "OCCURRENCES" | "DATE";

export interface Event {
    id: number;
    eventName: string;
    description?: string;
    eventStart: string;
    eventEnd: string;
    availabilityRulesDTO: {
        slotDurationMinutes: number;
        maxSlotsPerUser: number;
        bufferMinutes: number;
        minimumNoticeHours: number;
        maximumAdvanceDays: number;
        maxCapacity: number;
        allowCancellations: boolean;
        isPublic: boolean;
    };
    recurringRulesDTO?: {
        recurrenceFrequency: RecurrenceFrequency;
        interval: number;
        recurrenceDayOfWeek: number;
        recurrenceEndType: RecurrenceEndType;
        recurrenceOccurrences: number;
        recurrenceEndDate: string;
    };
    shareableId: string;
    bookingForm: BookingFormResponse;
    scheduleId: string;
    scheduleIsDefault: boolean;
}

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

export interface EventRequest {
    eventName: string;
    description?: string;
    eventStart: string;
    eventEnd: string;
    availabilityRulesDTO: {
        slotDurationMinutes: number;
        maxSlotsPerUser?: number;
        bufferMinutes?: number;
        minimumNoticeHours?: number;
        maximumAdvanceDays?: number;
        maxCapacity?: number;
        allowCancellations?: boolean;
        isPublic?: boolean;
    };
    recurringRulesDTO?: {
        recurrenceFrequency: RecurrenceFrequency;
        interval: number;
        recurrenceDayOfWeek: number;
        recurrenceEndType: RecurrenceEndType;
        recurrenceOccurrences: number;
        recurrenceEndDate: string;
    };
    bookingForm?: BookingFormRequest;
}

export interface AvailabilityRulesUpdateRequest {
    eventName?: string;
    description?: string;
    eventStart?: string;
    eventEnd?: string;
    slotDurationMinutes?: number;
    bufferMinutes?: number;
    minimumNoticeHours?: number;
    maximumAdvanceDays?: number;
    maxCapacity?: number;
    maxSlotsPerUser?: number;
    allowCancellations?: boolean;
    isPublic?: boolean;
}
