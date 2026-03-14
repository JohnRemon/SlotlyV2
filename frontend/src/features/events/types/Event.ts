import type {
    BookingFormRequest,
    BookingFormResponse,
} from "@/features/booking-page/types/BookingForms";
import type { ScheduleResponse } from "@/features/schedule/types/Schedule";

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

export interface EventResponse {
    id: number;
    eventName: string;
    description?: string;
    shareableId: string;
    scheduleId: string;
    scheduleIsDefault: boolean;
    availabilityRules: AvailabilityRules;
    bookingForm: BookingFormResponse;
    schedule: ScheduleResponse;
}

export interface PublicEventResponse {
    eventName: string;
    description?: string;
    host: {
        firstName: string;
        lastName: string;
    };
    slotDurationMinutes: number;
    bookingForm: BookingFormResponse;
    schedule: ScheduleResponse;
}

export interface EventRequest {
    eventName: string;
    description?: string;
    availabilityRules: Partial<AvailabilityRules>;
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
