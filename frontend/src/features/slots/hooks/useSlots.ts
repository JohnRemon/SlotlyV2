import { useQuery } from "@tanstack/react-query";
import { SlotsApi } from "../api/SlotsApi";

const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;

export const slotKeys = {
    all: ["slots"] as const,
    paged: (eventId: number, page: number, size: number) =>
        ["slots", "event", eventId, { page, size, timeZone }] as const,
    detail: (id: number) => ["slots", id, { timeZone }] as const,
    available: (
        shareableId: string,
        date: string,
        page: number,
        size: number,
    ) =>
        [
            "slots",
            "available",
            shareableId,
            date,
            { page, size, timeZone },
        ] as const,
};

export const useSlots = (eventId: number, page = 0, size = 20) => {
    return useQuery({
        queryKey: slotKeys.paged(eventId, page, size),
        queryFn: () => SlotsApi.getAll(eventId, page, size),
        select: (response) => response.data.content,
        enabled: !!eventId,
    });
};

export const useSlot = (id: number) => {
    return useQuery({
        queryKey: slotKeys.detail(id),
        queryFn: () => SlotsApi.getById(id),
        select: (response) => response.data.data,
        enabled: !!id,
    });
};

export const useAvailableSlots = (
    shareableId: string,
    date: string,
    page = 0,
    size = 20,
) => {
    return useQuery({
        queryKey: slotKeys.available(shareableId, date, page, size),
        queryFn: () => SlotsApi.getAvailable(shareableId, date, page, size),
        select: (response) => response.data.content,
        enabled: !!shareableId && !!date,
    });
};
