import type { DataResponse, PagedResponse } from "@/types/api";
import API from "../../../lib/api";
import type {
    Booking,
    CancelBookingRequest,
    CreateBookingRequest,
} from "../types/Booking";

export const createBooking = async (
    payload: CreateBookingRequest,
): Promise<DataResponse<Booking>> => {
    const res = await API.post("/api/v1/bookings", payload);
    return res.data.data;
};

export const getBooking = async (
    id: number,
): Promise<DataResponse<Booking>> => {
    const res = await API.get("/api/v1/bookings", { params: { id } });
    return res.data.data;
};

export const getBookings = async (
    page = 0,
    size = 10,
): Promise<PagedResponse<Booking>> => {
    const res = await API.get("/api/v1/bookings/me", {
        params: { page, size },
    });
    return res.data.content;
};

export const cancelBooking = async (payload: CancelBookingRequest) => {
    await API.patch("/api/v1/bookings/cancel", payload);
};

export const noShow = async (id: number) => {
    await API.post("/api/v1/bookings/no-show", {
        params: {
            id,
        },
    });
};
