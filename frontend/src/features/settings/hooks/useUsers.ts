import { useMutation, useQueryClient } from "@tanstack/react-query";
import { UserApi } from "../api/UserApi";
import { userQueryKey } from "@/features/auth/constants/queryKeys";

export const useUpdateFirstName = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ id, firstName }: { id: number; firstName: string }) =>
            UserApi.updateFirstName(id, firstName),
        onSuccess: (response) => {
            queryClient.setQueryData(userQueryKey, response.data.data);
            queryClient.invalidateQueries({
                queryKey: userQueryKey,
            });
        },
    });
};
export const useUpdateLastName = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ id, lastName }: { id: number; lastName: string }) =>
            UserApi.updateLastName(id, lastName),
        onSuccess: (response) => {
            queryClient.setQueryData(userQueryKey, response.data.data);
            queryClient.invalidateQueries({
                queryKey: userQueryKey,
            });
        },
    });
};
export const useUpdateTimeZone = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ id, timeZone }: { id: number; timeZone: string }) =>
            UserApi.updateTimeZone(id, timeZone),
        onSuccess: (response) => {
            queryClient.setQueryData(userQueryKey, response.data.data);
            queryClient.invalidateQueries({
                queryKey: userQueryKey,
            });
        },
    });
};
