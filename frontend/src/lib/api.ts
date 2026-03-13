import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_BASE_URL;
const AUTH_ME_PATH = "/api/v1/auth/me";

const isPublicAuthRoute = (pathname: string) =>
    pathname === "/login" ||
    pathname === "/register" ||
    pathname.startsWith("/forgot-password") ||
    pathname.startsWith("/book/");

const API = axios.create({
    baseURL: BASE_URL,
    withCredentials: true,
    headers: {
        "Content-Type": "application/json",
    },
    xsrfCookieName: "XSRF-TOKEN",
    xsrfHeaderName: "X-XSRF-TOKEN",
});

API.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            const requestUrl = String(error.config?.url ?? "");
            const currentPath = window.location.pathname;

            if (
                !requestUrl.includes(AUTH_ME_PATH) &&
                !isPublicAuthRoute(currentPath) &&
                currentPath !== "/login"
            ) {
                window.location.href = "/login";
            }
        }
        return Promise.reject(error);
    },
);

export default API;
