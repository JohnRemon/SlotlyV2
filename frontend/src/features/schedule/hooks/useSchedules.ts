import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { SchedulesApi } from "../api/SchedulesApi";
import type { UpdateScheduleRequest } from "../types/Schedule";

export const scheduleKeys = {
    all: ["schedules"] as const,
    paged: (page: number, size: number) =>
        ["schedules", { page, size }] as const,
    detail: (id: string) => ["schedules", id] as const,
};

export const useSchedules = (page = 0, size = 10) => {
    return useQuery({
        queryKey: scheduleKeys.paged(page, size),
        queryFn: () => SchedulesApi.getAll(page, size),
        select: (response) => response.data.content,
    });
};

export const useSchedule = (id: string) => {
    return useQuery({
        queryKey: scheduleKeys.detail(id),
        queryFn: () => SchedulesApi.getById(id),
        select: (response) => response.data.data,
        enabled: !!id,
    });
};

export const useCreateSchedule = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: SchedulesApi.create,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: scheduleKeys.all });
        },
    });
};

export const useUpdateScheduleName = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ id, name }: { id: string; name: string }) =>
            SchedulesApi.updateName(id, name),
        onSuccess: (_, { id }) => {
            queryClient.invalidateQueries({
                queryKey: scheduleKeys.detail(id),
            });
        },
    });
};

export const useUpdateScheduleDays = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({
            id,
            request,
        }: {
            id: string;
            request: UpdateScheduleRequest;
        }) => SchedulesApi.updateDays(id, request),
        onSuccess: (_, { id }) => {
            queryClient.invalidateQueries({
                queryKey: scheduleKeys.detail(id),
            });
            queryClient.invalidateQueries({
                queryKey: scheduleKeys.all,
            });
        },
    });
};

export const useUpdateScheduleDefault = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (id: string) => SchedulesApi.updateDefault(id),
        onSuccess: (_, id) => {
            queryClient.invalidateQueries({
                queryKey: scheduleKeys.detail(id),
            });
            queryClient.invalidateQueries({
                queryKey: scheduleKeys.all,
            });
        },
    });
};

export const useDeleteSchedule = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (id: string) => SchedulesApi.delete(id),
        onSuccess: (_, id) => {
            queryClient.invalidateQueries({ queryKey: scheduleKeys.all });
            queryClient.removeQueries({ queryKey: scheduleKeys.detail(id) });
        },
    });
};
