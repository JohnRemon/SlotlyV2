import type { DataResponse, PagedResponse } from "@/types/api";
import API from "../../../lib/api";
import type {
    ScheduleResponse,
    ScheduleRequest,
    UpdateScheduleRequest,
} from "../types/Schedule";

export const SchedulesApi = {
    getAll: (page = 0, size = 10) =>
        API.get<PagedResponse<ScheduleResponse>>("/api/v1/schedules", {
            params: { page, size },
        }),

    getById: (id: string) =>
        API.get<DataResponse<ScheduleResponse>>(`/api/v1/schedules/${id}`),

    create: (payload: ScheduleRequest) =>
        API.post<DataResponse<ScheduleResponse>>("/api/v1/schedules", payload),

    updateDays: (id: string, payload: UpdateScheduleRequest) =>
        API.patch<DataResponse<ScheduleResponse>>(
            `/api/v1/schedules/${id}/days`,
            payload,
        ),

    updateName: (id: string, name: string) =>
        API.patch<DataResponse<ScheduleResponse>>(
            `/api/v1/schedules/${id}/name`,
            {
                params: { name },
            },
        ),

    updateDefault: (id: string) =>
        API.patch<DataResponse<ScheduleResponse>>(
            `/api/v1/schedules/${id}/default`,
        ),

    delete: (id: string) => API.delete(`/api/v1/schedules/${id}`),
};
