import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useAuthStore } from "../stores/authStore";

type Status = "processing" | "success" | "error";

interface TokenPayload {
  userId: number;
  email: string;
  exp: number;
  iat: number;
}

export const OAuthCallback = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState<Status>("processing");
  const [errorMessage, setErrorMessage] = useState("");
  const setAuth = useAuthStore((state) => state.setAuth);

  useEffect(() => {
    const handleOAuthCallback = async () => {
      try {
        // Get tokens from URL params (note: camelCase, not snake_case)
        const accessToken = searchParams.get("accessToken");
        const refreshToken = searchParams.get("refreshToken");
        const error = searchParams.get("error");

        // Check for OAuth error from backend
        if (error) {
          setStatus("error");
          setErrorMessage(decodeURIComponent(error));
          return;
        }

        // Validate tokens exist
        if (!accessToken || !refreshToken) {
          setStatus("error");
          setErrorMessage("Missing authentication tokens");
          return;
        }

        // Decode and validate JWT payload
        const payload = decodeJWT(accessToken);
        if (!payload) {
          setStatus("error");
          setErrorMessage("Invalid token format");
          return;
        }

        // Check if token is expired
        if (isTokenExpired(payload)) {
          setStatus("error");
          setErrorMessage("Token has expired");
          return;
        }

        // Validate required fields
        if (!payload.userId || !payload.email) {
          setStatus("error");
          setErrorMessage("Invalid token data");
          return;
        }

        // Store authentication
        setAuth(accessToken, refreshToken, {
          id: payload.userId,
          email: payload.email,
        });

        setStatus("success");

        // Redirect to intended destination or dashboard
        const returnTo = sessionStorage.getItem("returnTo") || "/dashboard";
        sessionStorage.removeItem("returnTo");

        setTimeout(() => {
          navigate(returnTo, { replace: true });
        }, 1000);
      } catch (err) {
        console.error("OAuth callback error:", err);
        setStatus("error");
        setErrorMessage(
          err instanceof Error ? err.message : "Authentication failed",
        );
      }
    };

    handleOAuthCallback();
  }, [searchParams, navigate, setAuth]);

  return (
    <div className="flex min-h-screen flex-col items-center justify-center p-5">
      {status === "processing" && (
        <div className="text-center">
          <div className="mb-4 h-12 w-12 animate-spin rounded-full border-4 border-gray-300 border-t-blue-600"></div>
          <p className="text-lg text-gray-700">Processing authentication...</p>
        </div>
      )}

      {status === "success" && (
        <div className="text-center">
          <div className="mb-4 text-6xl">✓</div>
          <h2 className="mb-2 text-2xl font-bold text-green-600">
            Authentication Successful!
          </h2>
          <p className="text-gray-600">Redirecting to dashboard...</p>
        </div>
      )}

      {status === "error" && (
        <div className="max-w-md text-center">
          <div className="mb-4 text-6xl">✗</div>
          <h2 className="mb-2 text-2xl font-bold text-red-600">
            Authentication Failed
          </h2>
          <p className="mb-6 text-gray-700">
            {errorMessage || "An error occurred during authentication"}
          </p>
          <button
            onClick={() => navigate("/login", { replace: true })}
            className="rounded-lg bg-blue-600 px-6 py-2 text-white hover:bg-blue-700"
          >
            Back to Login
          </button>
        </div>
      )}
    </div>
  );
};

function decodeJWT(token: string): TokenPayload | null {
  try {
    const parts = token.split(".");
    if (parts.length !== 3) {
      return null;
    }

    const payload = parts[1];
    const decoded = JSON.parse(atob(payload));
    return decoded as TokenPayload;
  } catch (err) {
    console.error("Failed to decode JWT:", err);
    return null;
  }
}

function isTokenExpired(payload: TokenPayload): boolean {
  if (!payload.exp) {
    return false; // If no expiration, assume valid
  }

  const now = Math.floor(Date.now() / 1000);
  return payload.exp < now;
}
