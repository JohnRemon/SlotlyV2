import type { DataResponse, PagedResponse } from "@/types/api";
import API from "../../../lib/api";
import type {
    BlockedPeriod,
    BlockedPeriodRequest,
} from "../types/BlockedPeriod";

export const getBlockedPeriods = async (
    page = 0,
    size = 10,
): Promise<PagedResponse<BlockedPeriod>> => {
    const res = await API.get("/api/v1/blocked-periods", {
        params: { page, size },
    });
    return res.data.content;
};

export const getBlockedPeriod = async (
    id: string,
): Promise<DataResponse<BlockedPeriod>> => {
    const res = await API.get("/api/v1/blocked-periods", { params: { id } });
    return res.data.data;
};

export const createBlockedPeriod = async (
    request: BlockedPeriodRequest,
): Promise<DataResponse<BlockedPeriod>> => {
    const res = await API.post("/api/v1/blocked-periods", request);
    return res.data.data;
};

export const deleteBlockedPeriod = async (id: string) => {
    await API.delete("/api/v1/blocked-periods", { params: { id } });
};
