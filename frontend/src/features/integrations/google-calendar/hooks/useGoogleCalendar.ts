import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { GoogleCalendarApi } from "../api/GoogleCalendarApi";

export const calendarKeys = {
    all: ["calendar"] as const,
    status: () => ["calendar", "status"] as const,
};

export const useCalendarStatus = () => {
    return useQuery({
        queryKey: calendarKeys.status(),
        queryFn: () => GoogleCalendarApi.getConnectionStatus(),
        select: (response) => response.data.data,
    });
};

export const useInitiateConnection = () => {
    return useMutation({
        mutationFn: GoogleCalendarApi.initiateConnection,
        onSuccess: (response) => {
            window.location.href = response.data.data.authorizationUrl;
        },
    });
};

export const useExchangeAuthorizationCode = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: GoogleCalendarApi.exchangeAuthorizationCode,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: calendarKeys.status() });
        },
    });
};

export const useDisconnectCalendar = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: GoogleCalendarApi.disconnectCalendar,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: calendarKeys.status() });
        },
    });
};
