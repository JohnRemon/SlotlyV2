import API from "@/lib/api";
import type {
    BookingFormRequest,
    BookingFormResponse,
} from "@/features/booking-page/types/BookingForms";
import type { DataResponse, PagedResponse } from "@/types/api";
import type {
    AvailabilityRulesUpdateRequest,
    EventRequest,
    EventResponse,
    PublicEventResponse,
} from "../types/Event";

export const EventsApi = {
    getAll: (page = 0, size = 10) =>
        API.get<PagedResponse<EventResponse>>("/api/v1/events", {
            params: { page, size },
        }),

    getById: (id: number) =>
        API.get<DataResponse<EventResponse>>(`/api/v1/events/${id}`),

    getByScheduleId: (scheduleId: string, page = 0, size = 10) =>
        API.get<PagedResponse<EventResponse>>("/api/v1/events/by-schedule", {
            params: { scheduleId, page, size },
        }),

    getByShareableId: (shareableId: string) =>
        API.get<DataResponse<PublicEventResponse>>("/api/v1/events/public", {
            params: { shareableId },
        }),

    create: (data: EventRequest) =>
        API.post<DataResponse<EventResponse>>("/api/v1/events", data),

    update: (id: number, data: EventRequest) =>
        API.put<DataResponse<EventResponse>>(`/api/v1/events/${id}`, data),

    updateAvailabilityRules: (
        id: number,
        data: AvailabilityRulesUpdateRequest,
    ) =>
        API.patch<DataResponse<EventResponse>>(
            `/api/v1/events/${id}/availability-rules`,
            data,
        ),

    updateSchedule: (id: number, scheduleId: string) =>
        API.patch<DataResponse<EventResponse>>(
            `/api/v1/events/${id}/schedule`,
            null,
            {
                params: { scheduleId },
            },
        ),

    updateVisibility: (id: number, isPublic: boolean) =>
        API.patch<DataResponse<EventResponse>>(
            `/api/v1/events/${id}/availability-rules`,
            {
                isPublic,
            },
        ),

    updateBookingForm: (eventId: number, data: BookingFormRequest) =>
        API.put<DataResponse<BookingFormResponse>>(
            `/api/v1/events/${eventId}/booking-form`,
            data,
        ),

    delete: (id: number) => API.delete(`/api/v1/events/${id}`),
};
