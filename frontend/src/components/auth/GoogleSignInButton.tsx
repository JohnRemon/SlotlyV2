import React, { useCallback, useEffect, useRef, useState } from "react";
import { useAuthStore } from "@/stores/authStore";
import { authApi } from "@/services/authApi";

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID as string;

interface GoogleSignInButtonProps {
  onSuccess?: () => void;
  onError?: (error: string) => void;
}

export const GoogleSignInButton: React.FC<GoogleSignInButtonProps> = ({
  onSuccess,
  onError,
}) => {
  const setAuth = useAuthStore((state) => state.setAuth);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const buttonRef = useRef<HTMLDivElement>(null);
  const initializedRef = useRef(false);

  const handleCredentialResponse = useCallback(
    async (response: google.accounts.id.CredentialResponse) => {
      setLoading(true);
      setError(null);

      try {
        const { data: apiResponse } = await authApi.loginWithGoogle(response.credential);
        const authData = apiResponse.data;

        setAuth(authData.accessToken, authData.refreshToken, authData.user);
        onSuccess?.();
      } catch (err) {
        const message =
          err instanceof Error ? err.message : "Google sign-in failed";
        setError(message);
        onError?.(message);
      } finally {
        setLoading(false);
      }
    },
    [setAuth, onSuccess, onError],
  );

  useEffect(() => {
    if (!window.google?.accounts?.id || initializedRef.current) return;

    google.accounts.id.initialize({
      client_id: GOOGLE_CLIENT_ID,
      callback: handleCredentialResponse,
      auto_select: false,
      cancel_on_tap_outside: true,
    });

    if (buttonRef.current) {
      google.accounts.id.renderButton(buttonRef.current, {
        type: "standard",
        theme: "outline",
        size: "large",
        text: "continue_with",
        shape: "rectangular",
        width: 300,
      });
      initializedRef.current = true;
    }
  }, [handleCredentialResponse]);

  return (
    <div>
      <div
        ref={buttonRef}
        style={{
          opacity: loading ? 0.7 : 1,
          pointerEvents: loading ? "none" : "auto",
        }}
      />

      {loading && (
        <p
          style={{
            textAlign: "center",
            marginTop: "8px",
            fontSize: "14px",
            color: "#666",
          }}
        >
          Signing in...
        </p>
      )}

      {error && (
        <p
          style={{
            textAlign: "center",
            marginTop: "8px",
            fontSize: "14px",
            color: "#dc2626",
          }}
        >
          {error}
        </p>
      )}
    </div>
  );
};
