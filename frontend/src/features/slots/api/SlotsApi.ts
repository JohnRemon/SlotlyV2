import API from "@/lib/api";
import type { DataResponse, PagedResponse } from "@/types/api";
import type { SlotResponse } from "../types/Slots";

const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;

export const SlotsApi = {
    getAll: (eventId: number, page = 0, size = 20) =>
        API.get<PagedResponse<SlotResponse>>("/api/v1/slots", {
            params: { eventId, timeZone, page, size },
        }),

    getById: (id: number) =>
        API.get<DataResponse<SlotResponse>>(`/api/v1/slots/${id}`, {
            params: { id, timeZone },
        }),

    getAvailable: (shareableId: string, date: string, page = 0, size = 300) =>
        API.get<PagedResponse<SlotResponse>>("/api/v1/slots/available", {
            params: { shareableId, date, timeZone, page, size },
        }),
};
