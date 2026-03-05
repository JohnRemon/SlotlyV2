import { useState, useEffect } from "react";
import { AuthContext } from "../context/AuthContext";
import {
    getcurrentuser,
    logout as logoutUser,
    login as loginUser,
    loginWithGoogle as loginUserWithGoogle,
} from "../api/AuthApi";
import type { User } from "../types/AuthContextType";

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        getcurrentuser()
            .then((currentUser) => {
                setUser(currentUser ?? null);
            })
            .catch(() => setUser(null))
            .finally(() => setIsLoading(false));
    }, []);

    const login = async (email: string, password: string) => {
        const currentUser = await loginUser(email, password);
        setUser(currentUser.data.data);
    };

    const loginWithGoogle = async (idToken: string) => {
        const currentUser = await loginUserWithGoogle(idToken);
        setUser(currentUser.data.data);
    };

    const logout = async () => {
        logoutUser();
        setUser(null);
    };

    return (
        <AuthContext.Provider
            value={{ user, isLoading, login, loginWithGoogle, logout }}
        >
            {children}
        </AuthContext.Provider>
    );
}
