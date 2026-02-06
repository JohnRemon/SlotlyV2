import { Outlet, useNavigate } from "react-router-dom";
import { useAuthStore } from "@/stores/authStore";
import { authApi } from "@/services/authApi";

export function DashboardLayout() {
  const user = useAuthStore((s) => s.user);
  const logout = useAuthStore((s) => s.logout);
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await authApi.logout();
    } catch {
    } finally {
      logout();
      navigate("/login", { replace: true });
    }
  };

  return (
    <div style={{ minHeight: "100vh" }}>
      <header
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          padding: "16px 24px",
          borderBottom: "1px solid #e5e7eb",
        }}
      >
        <h1 style={{ fontSize: "20px", fontWeight: 600, margin: 0 }}>Slotly</h1>
        <div style={{ display: "flex", alignItems: "center", gap: "16px" }}>
          {user && (
            <span style={{ fontSize: "14px", color: "#6b7280" }}>
              {user.firstName} {user.lastName}
            </span>
          )}
          <button
            onClick={handleLogout}
            style={{
              fontSize: "14px",
              padding: "6px 12px",
              cursor: "pointer",
              background: "none",
              border: "1px solid #d1d5db",
              borderRadius: "6px",
            }}
          >
            Logout
          </button>
        </div>
      </header>
      <main style={{ padding: "24px" }}>
        <Outlet />
      </main>
    </div>
  );
}
