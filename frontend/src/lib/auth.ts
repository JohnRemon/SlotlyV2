import API from "./api";

export async function login(email: string, password: string) {
    return API.post("/api/v1/auth/login", { email, password });
}

export async function logout() {
    return API.post("/api/v1/auth/logout");
}
