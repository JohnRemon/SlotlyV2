import API from "../../../lib/api";
import type {
    BlockedPeriod,
    BlockedPeriodRequest,
} from "../types/BlockedPeriod";

export const getBlockedPeriods = async (): Promise<BlockedPeriod[]> => {
    const res = await API.get("/api/v1/blocked-periods");
    return res.data.data;
};

export const getBlockedPeriodById = async (
    id: string,
): Promise<BlockedPeriod> => {
    const res = await API.get(`/api/v1/blocked-periods/${id}`);
    return res.data.data;
};

export const createBlockedPeriod = async (
    request: BlockedPeriodRequest,
): Promise<BlockedPeriod> => {
    const res = await API.post("/api/v1/blocked-periods", request);
    return res.data.data;
};

export const deleteBlockedPeriod = async (id: string) => {
    await API.delete(`/api/v1/blocked-periods/${id}`);
};
