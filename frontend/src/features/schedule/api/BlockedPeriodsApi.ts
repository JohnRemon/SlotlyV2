import type { DataResponse, PagedResponse } from "@/types/api";
import API from "../../../lib/api";
import type {
    BlockedPeriodRequest,
    BlockedPeriodResponse,
} from "../types/BlockedPeriod";

export const BlockedPeriodsApi = {
    getAll: (page = 0, size = 10) =>
        API.get<PagedResponse<BlockedPeriodResponse>>(
            "/api/v1/blocked-periods",
            {
                params: { page, size },
            },
        ),

    getById: (id: string) =>
        API.get<DataResponse<BlockedPeriodResponse>>(
            `/api/v1/blocked-periods/${id}`,
        ),

    create: (payload: BlockedPeriodRequest) =>
        API.post<DataResponse<BlockedPeriodResponse>>(
            "/api/v1/blocked-periods",
            payload,
        ),

    delete: (id: string) => API.delete(`/api/v1/blocked-periods/${id}`),
};
