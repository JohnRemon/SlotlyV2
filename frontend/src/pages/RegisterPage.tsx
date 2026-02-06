import { useState } from "react";
import { Link } from "react-router-dom";
import { authApi } from "@/services/authApi";
import { getApiErrorMessage } from "@/lib/apiClient";

export function RegisterPage() {
  const [form, setForm] = useState({
    email: "",
    password: "",
    firstName: "",
    lastName: "",
    timeZone: Intl.DateTimeFormat().resolvedOptions().timeZone,
  });
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const updateField = (field: string, value: string) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e: React.SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await authApi.register(form);
      setSuccess(true);
    } catch (err) {
      setError(getApiErrorMessage(err, "Registration failed"));
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <>
        <h1 style={{ marginBottom: "16px", fontSize: "24px", fontWeight: 600 }}>
          Check your email
        </h1>
        <p
          style={{
            fontSize: "14px",
            color: "#6b7280",
            maxWidth: "300px",
            textAlign: "center",
          }}
        >
          We sent a verification link to <strong>{form.email}</strong>. Please
          verify your email before signing in.
        </p>
        <Link
          to="/login"
          style={{
            marginTop: "24px",
            color: "#2563eb",
            fontSize: "14px",
          }}
        >
          Back to login
        </Link>
      </>
    );
  }

  const inputStyle = {
    padding: "10px 12px",
    borderRadius: "6px",
    border: "1px solid #d1d5db",
    fontSize: "14px",
    width: "100%",
    boxSizing: "border-box" as const,
  };

  return (
    <>
      <h1 style={{ marginBottom: "24px", fontSize: "24px", fontWeight: 600 }}>
        Create your account
      </h1>

      <form
        onSubmit={handleSubmit}
        style={{
          width: "100%",
          maxWidth: "300px",
          display: "flex",
          flexDirection: "column",
          gap: "12px",
        }}
      >
        <div style={{ display: "flex", gap: "8px" }}>
          <input
            type="text"
            placeholder="First name"
            value={form.firstName}
            onChange={(e) => updateField("firstName", e.target.value)}
            required
            style={inputStyle}
          />
          <input
            type="text"
            placeholder="Last name"
            value={form.lastName}
            onChange={(e) => updateField("lastName", e.target.value)}
            required
            style={inputStyle}
          />
        </div>
        <input
          type="email"
          placeholder="Email"
          value={form.email}
          onChange={(e) => updateField("email", e.target.value)}
          required
          style={inputStyle}
        />
        <input
          type="password"
          placeholder="Password (min 8 chars)"
          value={form.password}
          onChange={(e) => updateField("password", e.target.value)}
          required
          minLength={8}
          style={inputStyle}
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
          {loading ? "Creating account..." : "Create account"}
        </button>

        {error && (
          <p
            style={{
              color: "#dc2626",
              fontSize: "14px",
              textAlign: "center",
              margin: 0,
            }}
          >
            {error}
          </p>
        )}
      </form>

      <p style={{ marginTop: "24px", fontSize: "14px", color: "#6b7280" }}>
        Already have an account?{" "}
        <Link to="/login" style={{ color: "#2563eb" }}>
          Sign in
        </Link>
      </p>
    </>
  );
}
