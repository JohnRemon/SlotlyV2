import API from "../../../lib/api";
import type { BookingFormRequest } from "../../bookings/types/Booking";
import type {
    AvailabilityRulesUpdateRequest,
    Event,
    EventRequest,
} from "../types/Event";

export const createEvent = async (payload: EventRequest): Promise<Event> => {
    const res = await API.post("/api/v1/events", payload);
    return res.data.data;
};

export const getEvents = async (): Promise<Event[]> => {
    const res = await API.get("/api/v1/events");
    return res.data.data.content;
};

export const getEventsBySchedule = async (id: string): Promise<Event[]> => {
    const res = await API.get("/api/v1/events/by-schedule", {
        params: { id },
    });
    return res.data.data;
};

export const getEvent = async (id: number): Promise<Event> => {
    const res = await API.get(`/api/v1/events/${id}`);
    return res.data.data;
};

export const updateEvent = async (
    payload: EventRequest,
    id: number,
): Promise<Event> => {
    const res = await API.put(`/api/v1/events/${id}`, payload);
    return res.data.data;
};

export const updateBookingForm = async (
    payload: BookingFormRequest,
    id: number,
): Promise<Event> => {
    const res = await API.patch(`/api/v1/events/${id}/booking-form`, payload);
    return res.data.data;
};

export const updateAvailabilityRules = async (
    payload: AvailabilityRulesUpdateRequest,
    id: number,
): Promise<Event> => {
    const res = await API.patch(
        `/api/v1/events/${id}/availability-rules`,
        payload,
    );
    return res.data.data;
};

export const updateSchedule = async (
    id: number,
    scheduleId: string,
): Promise<Event> => {
    const res = await API.patch(`/api/v1/events/${id}/schedule`, null, {
        params: { scheduleId },
    });
    return res.data.data;
};

export const deleteEvent = async (id: number) => {
    await API.delete(`/api/v1/events/${id}`);
};
