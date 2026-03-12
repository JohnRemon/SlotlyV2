import type { DataResponse, PagedResponse } from "@/types/api";
import API from "@/lib/api";
import type {
    BookingResponse,
    CancelBookingRequest,
    CreateBookingRequest,
} from "../types/Booking";

const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;

export const BookingsApi = {
    getAll: (page = 0, size = 10) =>
        API.get<PagedResponse<BookingResponse>>("/api/v1/bookings", {
            params: { page, size, timeZone },
        }),

    getById: (id: number) =>
        API.get<DataResponse<BookingResponse>>(`/api/v1/bookings/${id}`, {
            params: { timeZone },
        }),

    create: (payload: CreateBookingRequest) =>
        API.post<DataResponse<BookingResponse>>("/api/v1/bookings", payload, {
            params: { timeZone },
        }),

    noShow: (id: number) => API.post(`/api/v1/bookings/${id}/no-show`),

    cancel: (id: number, payload: CancelBookingRequest) =>
        API.patch(`/api/v1/bookings/${id}/cancel`, payload),
};
