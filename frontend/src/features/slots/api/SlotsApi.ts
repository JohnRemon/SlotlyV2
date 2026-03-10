import API from "@/lib/api";
import type { DataResponse, PagedResponse } from "@/types/api";
import type { Slot } from "../types/Slots";

export const getSlots = async (
    eventId: number,
    page = 0,
    size = 10,
): Promise<PagedResponse<Slot>> => {
    const res = await API.get("/api/v1/slots", {
        params: { eventId, page, size },
    });
    return res.data.content;
};

export const getSlotById = async (id: number): Promise<DataResponse<Slot>> => {
    const res = await API.get("/api/v1/slots", { params: { id } });
    return res.data.data;
};
export const getAvailableSlots = async (
    shareableId: string,
    date: Date,
    timezone: string,
    page = 0,
    size = 10,
): Promise<Slot> => {
    const res = await API.get("/api/v1/slots/available", {
        params: { shareableId, date, timezone, page, size },
    });
    return res.data.content;
};
