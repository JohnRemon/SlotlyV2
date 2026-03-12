import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { BlockedPeriodsApi } from "../api/BlockedPeriodsApi";

export const blockedPeriodKeys = {
    all: ["blocked-periods"] as const,
    paged: (page: number, size: number) =>
        ["blocked-periods", { page, size }] as const,
    detail: (id: string) => ["blocked-periods", id] as const,
};

export const useBlockedPeriods = (page = 0, size = 10) => {
    return useQuery({
        queryKey: blockedPeriodKeys.paged(page, size),
        queryFn: () => BlockedPeriodsApi.getAll(page, size),
        select: (response) => response.data.content,
    });
};

export const useBlockedPeriod = (id: string) => {
    return useQuery({
        queryKey: blockedPeriodKeys.detail(id),
        queryFn: () => BlockedPeriodsApi.getById(id),
        select: (response) => response.data.data,
        enabled: !!id,
    });
};

export const useCreateBlockedPeriod = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: BlockedPeriodsApi.create,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: blockedPeriodKeys.all });
        },
    });
};

export const useDeleteBlockedPeriod = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (id: string) => BlockedPeriodsApi.delete(id),
        onSuccess: (_, id) => {
            queryClient.invalidateQueries({ queryKey: blockedPeriodKeys.all });
            queryClient.removeQueries({
                queryKey: blockedPeriodKeys.detail(id),
            });
        },
    });
};
