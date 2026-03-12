import type { DataResponse } from "@/types/api";
import API from "../../../lib/api";
import type {
    LoginRequest,
    PasswordResetConfirmRequest,
    PasswordResetRequest,
    RegisterRequest,
    UserResponse,
} from "../types/Auth";

const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;

export const AuthApi = {
    login: (payload: LoginRequest) => API.post("/api/v1/auth/login", payload),

    loginWithGoogle: (idToken: string) =>
        API.post("/api/v1/auth/google", {
            params: { idToken, timeZone },
        }),

    logout: () => API.post("/api/v1/auth/logout"),

    register: (payload: RegisterRequest) =>
        API.post("/api/v1/users/register", payload),

    getCurrentUser: () => {
        API.get<DataResponse<UserResponse>>("/api/v1/auth/me");
    },

    forgotPassword: (payload: PasswordResetRequest) =>
        API.post("/api/v1/password-reset/request", payload),

    resetPassword: (payload: PasswordResetConfirmRequest, token: string) =>
        API.post("/api/v1/password-reset/confirm", payload, {
            params: {
                token,
            },
        }),
};
