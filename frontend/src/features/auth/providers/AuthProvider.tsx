import { useEffect, useState, type ReactNode } from "react";
import { AuthApi } from "../api/AuthApi";
import type { UserResponse } from "../types/Auth";
import { AuthContext } from "../context/AuthContext";

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<UserResponse | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        AuthApi.getCurrentUser()
            .then((response) => {
                setUser(response.data.data);
            })
            .catch(() => {
                setUser(null);
            })
            .finally(() => {
                setLoading(false);
            });
    }, []);

    const login = async (email: string, password: string) => {
        await AuthApi.login({ email, password });
        const response = await AuthApi.getCurrentUser();
        setUser(response.data.data);
    };

    const loginWithGoogle = async (idToken: string) => {
        await AuthApi.loginWithGoogle(idToken);
        const response = await AuthApi.getCurrentUser();
        setUser(response.data.data);
    };

    const logout = async () => {
        await AuthApi.logout();
        setUser(null);
    };

    return (
        <AuthContext.Provider
            value={{ user, loading, login, loginWithGoogle, logout, setUser }}
        >
            {children}
        </AuthContext.Provider>
    );
};
