import type { DataResponse, PagedResponse } from "@/types/api";
import API from "../../../lib/api";
import type {
    AvailabilityRulesUpdateRequest,
    Event,
    EventRequest,
    PublicEvent,
} from "../types/Event";
import type { BookingFormRequest } from "@/features/booking-page/types/BookingForms";

export const createEvent = async (
    payload: EventRequest,
): Promise<DataResponse<Event>> => {
    const res = await API.post("/api/v1/events", payload);
    return res.data.data;
};

export const createRecurringEvent = async (
    payload: EventRequest,
): Promise<DataResponse<Event>> => {
    const res = await API.post("/api/v1/events/recurring", payload);
    return res.data.data;
};

export const getEvents = async (
    page = 0,
    size = 10,
): Promise<PagedResponse<Event>> => {
    const res = await API.get("/api/v1/events", { params: { page, size } });
    return res.data.content;
};

export const getEventsBySchedule = async (
    id: string,
    page = 0,
    size = 10,
): Promise<PagedResponse<Event>> => {
    const res = await API.get("/api/v1/events/by-schedule", {
        params: { id, page, size },
    });
    return res.data.content;
};

export const getEvent = async (id: number): Promise<DataResponse<Event>> => {
    const res = await API.get(`/api/v1/events/${id}`);
    return res.data.data;
};

export const getPublicEvent = async (
    shareableId: string,
): Promise<DataResponse<PublicEvent>> => {
    const res = await API.get(`/api/v1/events/public/${shareableId}`);
    return res.data.data;
};

export const updateEvent = async (
    payload: EventRequest,
    id: number,
): Promise<DataResponse<Event>> => {
    const res = await API.put(`/api/v1/events/${id}`, payload);
    return res.data.data;
};

export const updateBookingForm = async (
    payload: BookingFormRequest,
    id: number,
): Promise<DataResponse<Event>> => {
    const res = await API.patch(`/api/v1/events/${id}/booking-form`, payload);
    return res.data.data;
};

export const updateAvailabilityRules = async (
    payload: AvailabilityRulesUpdateRequest,
    id: number,
): Promise<DataResponse<Event>> => {
    const res = await API.patch(
        `/api/v1/events/${id}/availability-rules`,
        payload,
    );
    return res.data.data;
};

export const updateSchedule = async (
    id: number,
    scheduleId: string,
): Promise<DataResponse<Event>> => {
    const res = await API.patch(`/api/v1/events/${id}/schedule`, null, {
        params: { scheduleId },
    });
    return res.data.data;
};

export const deleteEvent = async (id: number) => {
    await API.delete(`/api/v1/events/${id}`);
};
