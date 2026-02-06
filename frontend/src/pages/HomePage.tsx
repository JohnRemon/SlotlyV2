import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { GoogleSignInButton } from "@/components/auth/GoogleSignInButton";
import { authApi } from "@/services/authApi";
import { useAuthStore } from "@/stores/authStore";
import { getApiErrorMessage } from "@/lib/apiClient";

export function LoginPage() {
  const navigate = useNavigate();
  const setAuth = useAuthStore((s) => s.setAuth);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleGoogleSuccess = () => {
    navigate("/dashboard", { replace: true });
  };

  const handleSubmit = async (e: React.SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const { data: apiResponse } = await authApi.loginWithCredentials({ email, password });
      const authData = apiResponse.data;
      setAuth(authData.accessToken, authData.refreshToken, authData.user);
      navigate("/dashboard", { replace: true });
    } catch (err) {
      setError(getApiErrorMessage(err, "Login failed"));
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <h1 style={{ marginBottom: "24px", fontSize: "24px", fontWeight: 600 }}>
        Sign in to Slotly
      </h1>

      <form
        onSubmit={handleSubmit}
        style={{ width: "100%", maxWidth: "300px", display: "flex", flexDirection: "column", gap: "12px" }}
      >
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          style={{
            padding: "10px 12px",
            borderRadius: "6px",
            border: "1px solid #d1d5db",
            fontSize: "14px",
          }}
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          minLength={8}
          style={{
            padding: "10px 12px",
            borderRadius: "6px",
            border: "1px solid #d1d5db",
            fontSize: "14px",
          }}
        />
        <button
          type="submit"
          disabled={loading}
          style={{
            padding: "10px",
            borderRadius: "6px",
            border: "none",
            backgroundColor: "#2563eb",
            color: "white",
            fontSize: "14px",
            fontWeight: 500,
            cursor: loading ? "not-allowed" : "pointer",
            opacity: loading ? 0.7 : 1,
          }}
        >
          {loading ? "Signing in..." : "Sign in"}
        </button>

        {error && (
          <p style={{ color: "#dc2626", fontSize: "14px", textAlign: "center", margin: 0 }}>
            {error}
          </p>
        )}
      </form>

      <div
        style={{
          width: "100%",
          maxWidth: "300px",
          display: "flex",
          alignItems: "center",
          gap: "12px",
          margin: "16px 0",
        }}
      >
        <div style={{ flex: 1, height: "1px", backgroundColor: "#d1d5db" }} />
        <span style={{ fontSize: "12px", color: "#9ca3af" }}>or</span>
        <div style={{ flex: 1, height: "1px", backgroundColor: "#d1d5db" }} />
      </div>

      <div style={{ width: "100%", maxWidth: "300px" }}>
        <GoogleSignInButton
          onSuccess={handleGoogleSuccess}
          onError={(err) => setError(err)}
        />
      </div>

      <p style={{ marginTop: "24px", fontSize: "14px", color: "#6b7280" }}>
        Don&apos;t have an account?{" "}
        <Link to="/register" style={{ color: "#2563eb" }}>
          Sign up
        </Link>
      </p>
    </>
  );
}
