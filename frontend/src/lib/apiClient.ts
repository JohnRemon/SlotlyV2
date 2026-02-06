import axios, { AxiosError } from "axios";
import type {
  ApiResponse,
  RefreshTokenResponse,
} from "@/types";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
  xsrfCookieName: "XSRF-TOKEN",
  xsrfHeaderName: "X-XSRF-TOKEN",
});

export function getApiErrorMessage(err: unknown, fallback: string): string {
  if (!(err instanceof AxiosError) || !err.response?.data) {
    return fallback;
  }
  const body = err.response.data;
  if (Array.isArray(body.data) && body.data.length > 0) {
    return body.data.join(". ");
  }
  if (typeof body.message === "string" && body.message !== "Invalid Arguments") {
    return body.message;
  }
  return fallback;
}

let isRefreshing = false;
let failedQueue: Array<{
  resolve: (token: string) => void;
  reject: (error: unknown) => void;
}> = [];

function processQueue(error: unknown, token: string | null) {
  for (const { resolve, reject } of failedQueue) {
    if (error) {
      reject(error);
    } else {
      resolve(token!);
    }
  }
  failedQueue = [];
}

function getAuthStorage(): { accessToken: string | null; refreshToken: string | null } {
  try {
    const raw = localStorage.getItem("auth-storage");
    if (!raw) return { accessToken: null, refreshToken: null };
    const parsed = JSON.parse(raw) as { state?: { accessToken?: string; refreshToken?: string } };
    return {
      accessToken: parsed.state?.accessToken ?? null,
      refreshToken: parsed.state?.refreshToken ?? null,
    };
  } catch {
    return { accessToken: null, refreshToken: null };
  }
}

apiClient.interceptors.request.use((config) => {
  const { accessToken } = getAuthStorage();
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (
      error.response?.status !== 401 ||
      originalRequest._retry ||
      originalRequest.url?.includes("/api/v1/auth/")
    ) {
      return Promise.reject(error);
    }

    if (isRefreshing) {
      return new Promise<string>((resolve, reject) => {
        failedQueue.push({ resolve, reject });
      }).then((token) => {
        originalRequest.headers.Authorization = `Bearer ${token}`;
        return apiClient(originalRequest);
      });
    }

    originalRequest._retry = true;
    isRefreshing = true;

    try {
      const { refreshToken } = getAuthStorage();
      if (!refreshToken) {
        throw new Error("No refresh token available");
      }

      const { data } = await axios.post<ApiResponse<RefreshTokenResponse>>(
        `${API_BASE_URL}/api/v1/auth/refresh`,
        { refreshToken },
      );

      const newAccessToken = data.data.accessToken;

      const raw = localStorage.getItem("auth-storage");
      if (raw) {
        const parsed = JSON.parse(raw);
        parsed.state.accessToken = newAccessToken;
        localStorage.setItem("auth-storage", JSON.stringify(parsed));
      }

      processQueue(null, newAccessToken);
      originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
      return apiClient(originalRequest);
    } catch (refreshError) {
      processQueue(refreshError, null);
      localStorage.removeItem("auth-storage");
      window.location.href = "/login";
      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  },
);
