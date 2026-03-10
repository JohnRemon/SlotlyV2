import api from "../../../lib/api";

export async function login(email: string, password: string) {
    return api.post("/api/v1/auth/login", { email, password });
}

// TODO: pass timezone for google auth
export async function loginWithGoogle(idToken: string) {
    return api.post("/api/v1/auth/google", { idToken });
}

export async function logout() {
    return api.post("/api/v1/auth/logout");
}

export async function register(payload: {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    timeZone: string;
}) {
    return api.post("/api/v1/users/register", payload);
}

export async function getcurrentuser() {
    const res = await api.get("/api/v1/auth/me");
    return res.data.data;
}

export async function forgotPassword(email: string) {
    return api.post("/api/v1/password-reset/request", { email });
}

export async function resetPassword(
    token: string,
    password: string,
    confirmPassword: string,
) {
    return api.post(`/api/v1/password-reset/confirm?token=${token}`, {
        password,
        confirmPassword,
    });
}
