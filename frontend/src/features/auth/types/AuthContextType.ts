import type { UserResponse } from "./Auth";

export interface AuthContextType {
    user: UserResponse | null;
    loading: boolean;
    login: (email: string, password: string) => Promise<void>;
    loginWithGoogle: (idToken: string) => Promise<void>;
    logout: () => Promise<void>;
    setUser: (user: UserResponse | null) => void;
}
