import type { Booking, CreateBookingRequest } from "../types/Booking";
import API from "../../../lib/api";

export const createBooking = async (
    payload: CreateBookingRequest,
): Promise<Booking> => {
    const res = await API.post("/api/v1/bookings", payload);
    return res.data.data;
};

export const getBooking = async (id: number): Promise<Booking> => {
    const res = await API.get(`/api/v1/bookings/${id}`);
    return res.data.data;
};

export const getBookings = async (): Promise<Booking[]> => {
    const res = await API.get("/api/v1/bookings/me");
    return res.data.data;
};

export const cancelBooking = async (
    id: number,
    attendeeEmail: string,
    reason: string,
): Promise<void> => {
    await API.patch(`/api/v1/bookings/${id}/cancel`, {
        attendeeEmail,
        cancellationReason: reason,
    });
};

export const noShow = async (id: number): Promise<void> => {
    await API.post(`/api/v1/bookings/${id}/no-show`);
};
