import { useQuery, useQueryClient } from "@tanstack/react-query";
import { type ReactNode } from "react";
import { AuthApi } from "../api/AuthApi";
import type { UserResponse } from "../types/Auth";
import { AuthContext } from "../context/AuthContext";
import API from "@/lib/api";
import { userQueryKey } from "../constants/queryKeys";

const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const queryClient = useQueryClient();

    const { data: user = null, isLoading: loading } = useQuery({
        queryKey: userQueryKey,
        queryFn: () => AuthApi.getCurrentUser().then((res) => res.data.data),
        retry: false,
        staleTime: 1000 * 60 * 5,
    });

    const login = async (email: string, password: string) => {
        await AuthApi.login({ email, password });
        await queryClient.invalidateQueries({ queryKey: userQueryKey });
    };

    const loginWithGoogle = async (idToken: string) => {
        await API.post("/api/v1/auth/google", null, {
            params: { idToken, timeZone },
        });
        await queryClient.invalidateQueries({ queryKey: userQueryKey });
    };

    const logout = async () => {
        await AuthApi.logout();
        queryClient.setQueryData(userQueryKey, null);
    };

    const setUser = (user: UserResponse | null) => {
        queryClient.setQueryData(userQueryKey, user);
    };

    return (
        <AuthContext.Provider
            value={{ user, loading, login, loginWithGoogle, logout, setUser }}
        >
            {children}
        </AuthContext.Provider>
    );
};
