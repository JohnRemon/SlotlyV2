import type {
    BookingFormRequest,
    BookingFormResponse,
} from "@/features/booking-page/types/BookingForms";

export type RecurrenceFrequency = "DAILY" | "WEEKLY" | "MONTHLY" | "CUSTOM";
export type RecurrenceEndType = "NEVER" | "OCCURRENCES" | "DATE";

export interface AvailabilityRules {
    slotDurationMinutes: number;
    maxSlotsPerUser?: number;
    bufferMinutes?: number;
    minimumNoticeHours?: number;
    maximumAdvanceDays?: number;
    maxCapacity?: number;
    allowsCancellations?: boolean;
    isPublic?: boolean;
}

export interface RecurringRules {
    recurrenceFrequency: RecurrenceFrequency;
    interval: number;
    recurrenceDayOfWeek: number;
    recurrenceEndType: RecurrenceEndType;
    recurrenceOccurrences: number;
    recurrenceEndDate: string;
}

export interface EventResponse {
    id: number;
    eventName: string;
    description?: string;
    eventStart: string;
    eventEnd: string;
    shareableId: string;
    scheduleId: string;
    scheduleIsDefault: boolean;
    availabilityRules: AvailabilityRules;
    recurringRules?: RecurringRules;
    bookingForm?: BookingFormResponse;
}

export interface PublicEventResponse {
    eventName: string;
    description?: string;
    eventStart: string;
    eventEnd: string;
    host: {
        firstName: string;
        lastName: string;
    };
    slotDurationMinutes: number;
    bookingForm: BookingFormResponse;
}

export interface EventRequest {
    eventName: string;
    description?: string;
    eventStart: string;
    eventEnd: string;
    availabilityRules: Partial<AvailabilityRules>;
    recurringRules?: RecurringRules;
    bookingForm?: BookingFormRequest;
}

export interface AvailabilityRulesUpdateRequest {
    slotDurationMinutes?: number;
    bufferMinutes?: number;
    minimumNoticeHours?: number;
    maximumAdvanceDays?: number;
    maxCapacity?: number;
    maxSlotsPerUser?: number;
    allowsCancellations?: boolean;
    isPublic?: boolean;
}
