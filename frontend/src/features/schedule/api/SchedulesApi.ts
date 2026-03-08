import API from "../../../lib/api";
import type {
    Schedule,
    ScheduleRequest,
    UpdateScheduleRequest,
} from "../types/Schedule";

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
    request: UpdateScheduleRequest,
    id: string,
): Promise<Schedule> => {
    const res = await API.put(`/api/v1/schedule/${id}`, request);
    return res.data.data;
};

export const updateScheduleName = async (
    name: string,
    id: string,
): Promise<Schedule> => {
    const res = await API.patch(`/api/v1/schedule/${id}/name`, null, {
        params: { name },
    });
    return res.data.data;
};

export const updateDefaultSchedule = async (id: string): Promise<Schedule> => {
    const res = await API.patch(`/api/v1/schedule/${id}/default`);
    return res.data.data;
};

export const deleteSchedule = async (id: string) => {
    await API.delete(`/api/v1/schedule/${id}`);
};
