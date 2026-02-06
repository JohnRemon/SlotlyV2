import { apiClient } from "@/lib/apiClient";
import type {
  ApiResponse,
  JwtAuthenticationResponse,
  LoginRequest,
  RegisterRequest,
  RefreshTokenResponse,
  User,
} from "@/types";

export const authApi = {
  loginWithCredentials(data: LoginRequest) {
    return apiClient.post<ApiResponse<JwtAuthenticationResponse>>(
      "/api/v1/auth/login",
      data,
    );
  },

  loginWithGoogle(idToken: string) {
    return apiClient.post<ApiResponse<JwtAuthenticationResponse>>(
      "/api/v1/auth/google",
      { idToken },
    );
  },

  register(data: RegisterRequest) {
    return apiClient.post<ApiResponse<User>>("/api/v1/users/register", data);
  },

  refreshToken(refreshToken: string) {
    return apiClient.post<ApiResponse<RefreshTokenResponse>>(
      "/api/v1/auth/refresh",
      { refreshToken },
    );
  },

  logout() {
    return apiClient.post<ApiResponse<null>>("/api/v1/users/logout");
  },

  verifyEmail(token: string) {
    return apiClient.post<ApiResponse<null>>(
      `/api/v1/users/verify-email?token=${encodeURIComponent(token)}`,
    );
  },

  getProfile() {
    return apiClient.get<ApiResponse<User>>("/api/v1/users/me");
  },
};
