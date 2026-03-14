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
        API.post("/api/v1/auth/google", null, {
            params: { idToken, timeZone },
        }),

    logout: () => API.post("/api/v1/auth/logout"),

    register: (payload: RegisterRequest) =>
        API.post("/api/v1/users/register", payload),

    getCurrentUser: () =>
        API.get<DataResponse<UserResponse>>("/api/v1/users/me"),

    forgotPassword: (payload: PasswordResetRequest) =>
        API.post("/api/v1/password-reset/request", payload),

    resetPassword: (payload: PasswordResetConfirmRequest, token: string) =>
        API.post("/api/v1/password-reset/confirm", payload, {
            params: {
                token,
            },
        }),

    verifyEmail: (token: string) =>
        API.post<DataResponse<UserResponse>>(
            "/api/v1/auth/verify-email/confirm",
            null,
            {
            params: { token },
            },
        ),

    resendVerificationEmail: (email: string) =>
        API.post("/api/v1/auth/verify-email/resend", null, {
            params: {
                email,
            },
        }),
};
