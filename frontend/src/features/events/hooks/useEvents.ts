import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { EventsApi } from "../api/EventsApi";
import type {
    EventRequest,
    AvailabilityRulesUpdateRequest,
} from "../types/Event";
import type { BookingFormRequest } from "@/features/booking-page/types/BookingForms";

export const eventKeys = {
    all: ["events"] as const,
    paged: (page: number, size: number) => ["events", { page, size }] as const,
    detail: (id: number) => ["events", id] as const,
    bySchedule: (scheduleId: string, page: number, size: number) =>
        ["events", "bySchedule", scheduleId, { page, size }] as const,
    public: (shareableId: string) => ["events", "public", shareableId] as const,
};

export const useEvents = (page = 0, size = 10) => {
    return useQuery({
        queryKey: eventKeys.paged(page, size),
        queryFn: () => EventsApi.getAll(page, size),
        select: (response) => response.data.content,
    });
};

export const useEvent = (id: number) => {
    return useQuery({
        queryKey: eventKeys.detail(id),
        queryFn: () => EventsApi.getById(id),
        select: (response) => response.data.data,
        enabled: !!id,
    });
};

export const useEventsBySchedule = (
    scheduleId: string,
    page = 0,
    size = 10,
) => {
    return useQuery({
        queryKey: eventKeys.bySchedule(scheduleId, page, size),
        queryFn: () => EventsApi.getByScheduleId(scheduleId, page, size),
        select: (response) => response.data.content,
        enabled: !!scheduleId,
    });
};

export const usePublicEvent = (shareableId: string) => {
    return useQuery({
        queryKey: eventKeys.public(shareableId),
        queryFn: () => EventsApi.getByShareableId(shareableId),
        select: (response) => response.data.data,
        enabled: !!shareableId,
    });
};

export const useCreateEvent = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: EventRequest) => EventsApi.create(data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: eventKeys.all });
        },
    });
};

export const useUpdateEvent = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ id, data }: { id: number; data: EventRequest }) =>
            EventsApi.update(id, data),
        onSuccess: (_, { id }) => {
            queryClient.invalidateQueries({ queryKey: eventKeys.detail(id) });
            queryClient.invalidateQueries({ queryKey: eventKeys.all });
        },
    });
};

export const useUpdateAvailabilityRules = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({
            id,
            data,
        }: {
            id: number;
            data: AvailabilityRulesUpdateRequest;
        }) => EventsApi.updateAvailabilityRules(id, data),
        onSuccess: (_, { id }) => {
            queryClient.invalidateQueries({ queryKey: eventKeys.detail(id) });
            queryClient.invalidateQueries({ queryKey: eventKeys.all });
        },
    });
};

export const useUpdateEventSchedule = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ id, scheduleId }: { id: number; scheduleId: string }) =>
            EventsApi.updateSchedule(id, scheduleId),
        onSuccess: (_, { id, scheduleId }) => {
            queryClient.invalidateQueries({ queryKey: eventKeys.detail(id) });
            queryClient.invalidateQueries({ queryKey: eventKeys.all });
            queryClient.invalidateQueries({
                queryKey: ["events", "bySchedule", scheduleId],
            });
        },
    });
};

export const useUpdateEventVisibility = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ id, isPublic }: { id: number; isPublic: boolean }) =>
            EventsApi.updateVisibility(id, isPublic),
        onSuccess: (_, { id }) => {
            queryClient.invalidateQueries({ queryKey: eventKeys.detail(id) });
            queryClient.invalidateQueries({ queryKey: eventKeys.all });
        },
    });
};

export const useUpdateBookingForm = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({
            eventId,
            data,
        }: {
            eventId: number;
            data: BookingFormRequest;
        }) => EventsApi.updateBookingForm(eventId, data),
        onSuccess: (_, { eventId }) => {
            queryClient.invalidateQueries({
                queryKey: eventKeys.detail(eventId),
            });
            // Also invalidate any booking form queries if you have them
        },
    });
};

export const useDeleteEvent = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (id: number) => EventsApi.delete(id),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: eventKeys.all });
        },
    });
};
