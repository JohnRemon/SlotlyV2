import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useAuthStore } from "@/stores/authStore";
import { authApi } from "@/services/authApi";

type Status = "processing" | "success" | "error";

export const OAuthCallback = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState<Status>("processing");
  const [errorMessage, setErrorMessage] = useState("");
  const setAuth = useAuthStore((state) => state.setAuth);

  useEffect(() => {
    const handleOAuthCallback = async () => {
      try {
        const accessToken = searchParams.get("accessToken");
        const refreshToken = searchParams.get("refreshToken");
        const error = searchParams.get("error");

        if (error) {
          setStatus("error");
          setErrorMessage(decodeURIComponent(error));
          return;
        }

        if (!accessToken || !refreshToken) {
          setStatus("error");
          setErrorMessage("Missing authentication tokens");
          return;
        }

        const { data: apiResponse } = await authApi.getProfile();
        const user = apiResponse.data;

        setAuth(accessToken, refreshToken, user);
        setStatus("success");

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
    <div
      style={{
        display: "flex",
        minHeight: "100vh",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        padding: "20px",
      }}
    >
      {status === "processing" && (
        <div style={{ textAlign: "center" }}>
          <p style={{ fontSize: "18px", color: "#374151" }}>
            Processing authentication...
          </p>
        </div>
      )}

      {status === "success" && (
        <div style={{ textAlign: "center" }}>
          <div style={{ fontSize: "48px", marginBottom: "16px" }}>&#10003;</div>
          <h2 style={{ fontSize: "24px", fontWeight: 700, color: "#16a34a", marginBottom: "8px" }}>
            Authentication Successful!
          </h2>
          <p style={{ color: "#6b7280" }}>Redirecting to dashboard...</p>
        </div>
      )}

      {status === "error" && (
        <div style={{ textAlign: "center", maxWidth: "400px" }}>
          <div style={{ fontSize: "48px", marginBottom: "16px" }}>&#10007;</div>
          <h2 style={{ fontSize: "24px", fontWeight: 700, color: "#dc2626", marginBottom: "8px" }}>
            Authentication Failed
          </h2>
          <p style={{ color: "#374151", marginBottom: "24px" }}>
            {errorMessage || "An error occurred during authentication"}
          </p>
          <button
            onClick={() => navigate("/login", { replace: true })}
            style={{
              padding: "8px 24px",
              borderRadius: "6px",
              border: "none",
              backgroundColor: "#2563eb",
              color: "white",
              fontSize: "14px",
              cursor: "pointer",
            }}
          >
            Back to Login
          </button>
        </div>
      )}
    </div>
  );
};
