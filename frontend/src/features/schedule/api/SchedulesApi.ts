import API from "../../../lib/api";
import type { Schedule, ScheduleRequest } from "../types/Schedule";

export const getScheduleById = async (id: string): Promise<Schedule> => {
    const res = await API.get(`/api/v1/schedule/${id}`);
    return res.data.data;
};

export const getSchedules = async (): Promise<Schedule[]> => {
    const res = await API.get("/api/v1/schedule");
    return res.data.data;
};

export const createSchedule = async (
    request: ScheduleRequest,
): Promise<Schedule> => {
    const res = await API.post("/api/v1/schedule", request);
    return res.data.data;
};

export const updateSchedule = async (
    request: ScheduleRequest,
    id: string,
): Promise<Schedule> => {
    const res = await API.put(`/api/v1/schedule/${id}`, request);
    return res.data.data;
};

export const deleteSchedule = async (id: string) => {
    await API.delete(`/api/v1/schedule/${id}`);
};
