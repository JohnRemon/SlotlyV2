import { useEffect, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { authApi } from "@/services/authApi";

export function VerifyEmailPage() {
  const [searchParams] = useSearchParams();
  const [status, setStatus] = useState<"loading" | "success" | "error">("loading");
  const [message, setMessage] = useState("");

  useEffect(() => {
    const token = searchParams.get("token");
    if (!token) {
      setStatus("error");
      setMessage("Missing verification token");
      return;
    }

    authApi
      .verifyEmail(token)
      .then(({ data }) => {
        setStatus("success");
        setMessage(data.message);
      })
      .catch(() => {
        setStatus("error");
        setMessage("Email verification failed. The link may have expired.");
      });
  }, [searchParams]);

  return (
    <>
      {status === "loading" && <p>Verifying your email...</p>}

      {status === "success" && (
        <>
          <h2 style={{ fontSize: "20px", fontWeight: 600, color: "#16a34a" }}>
            Email Verified
          </h2>
          <p style={{ fontSize: "14px", color: "#6b7280", marginTop: "8px" }}>{message}</p>
          <Link to="/login" style={{ marginTop: "16px", color: "#2563eb", fontSize: "14px" }}>
            Go to login
          </Link>
        </>
      )}

      {status === "error" && (
        <>
          <h2 style={{ fontSize: "20px", fontWeight: 600, color: "#dc2626" }}>
            Verification Failed
          </h2>
          <p style={{ fontSize: "14px", color: "#6b7280", marginTop: "8px" }}>{message}</p>
          <Link to="/login" style={{ marginTop: "16px", color: "#2563eb", fontSize: "14px" }}>
            Back to login
          </Link>
        </>
      )}
    </>
  );
}
