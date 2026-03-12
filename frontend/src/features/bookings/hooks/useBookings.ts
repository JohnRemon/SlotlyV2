import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { BookingsApi } from "../api/BookingsApi";
import type { CancelBookingRequest } from "../types/Booking";

export const bookingKeys = {
    all: ["bookings"] as const,
    paged: (page: number, size: number) =>
        ["bookings", { page, size }] as const,
    detail: (id: number) => ["bookings", id] as const,
};

export const useBookings = (page = 0, size = 10) => {
    return useQuery({
        queryKey: bookingKeys.paged(page, size),
        queryFn: () => BookingsApi.getAll(page, size),
        select: (response) => response.data.content,
    });
};

export const useBooking = (id: number) => {
    return useQuery({
        queryKey: bookingKeys.detail(id),
        queryFn: () => BookingsApi.getById(id),
        select: (response) => response.data.data,
        enabled: !!id,
    });
};

export const useCreateBooking = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: BookingsApi.create,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: bookingKeys.all });
        },
    });
};

export const useNoShow = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (id: number) => BookingsApi.noShow(id),
        onSuccess: (_, id) => {
            queryClient.invalidateQueries({ queryKey: bookingKeys.detail(id) });
            queryClient.invalidateQueries({ queryKey: bookingKeys.all });
        },
    });
};

export const useCancelBooking = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({
            id,
            request,
        }: {
            id: number;
            request: CancelBookingRequest;
        }) => BookingsApi.cancel(id, request),
        onSuccess: (_, { id }) => {
            queryClient.invalidateQueries({ queryKey: bookingKeys.detail(id) });
            queryClient.invalidateQueries({ queryKey: bookingKeys.all });
        },
    });
};
