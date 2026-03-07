import { useEffect, useState } from "react";
import type {
    BlockedPeriod,
    BlockedPeriodRequest,
} from "../types/BlockedPeriod";
import {
    createBlockedPeriod,
    deleteBlockedPeriod,
    getBlockedPeriods,
} from "../api/BlockedPeriodsApi";
import toast from "react-hot-toast";

export const useBlockedPeriods = () => {
    const [blockedPeriods, setBlockedPeriods] = useState<BlockedPeriod[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        getBlockedPeriods()
            .then(setBlockedPeriods)
            .catch((error) => toast.error(error.response?.data?.message))
            .finally(() => setIsLoading(false));
    }, []);

    const create = async (request: BlockedPeriodRequest) => {
        const newBlockedPeriod = await createBlockedPeriod(request);
        setBlockedPeriods((prev) => [newBlockedPeriod, ...prev]);
    };

    const remove = async (id: string) => {
        await deleteBlockedPeriod(id);
        setBlockedPeriods((prev) =>
            prev.filter((blockedPeriod) => blockedPeriod.id != id),
        );
    };

    return { blockedPeriods, isLoading, create, remove };
};
