import api from "../../../lib/api";

export async function login(email: string, password: string) {
    return api.post("/api/v1/auth/login", { email, password });
}

export async function loginWithGoogle(idToken: string) {
    return api.post("/api/v1/auth/google", { idToken });
}

export async function logout() {
    return api.post("/api/v1/auth/logout");
}

export async function register(payload: {
    email: string;
    password: string;
    firstname: string;
    lastname: string;
    timezone: string;
}) {
    return api.post("/api/v1/users/register", payload);
}

export async function getcurrentuser() {
    const res = await api.get("/api/v1/auth/me");
    return res.data.data;
}
