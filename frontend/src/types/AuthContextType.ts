export interface User {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
    timeZone: string;
}

export interface AuthContextType {
    user: User | null;
    isLoading: boolean;
    login: (email: string, password: string) => Promise<void>;
    loginWithGoogle: (idToken: string) => Promise<void>;
    logout: () => void;
}
