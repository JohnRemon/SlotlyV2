import { useEffect, useState } from "react";
import { authApi } from "@/services/authApi";
import { useAuthStore } from "@/stores/authStore";
import type { User } from "@/types";

export function DashboardPage() {
  const storedUser = useAuthStore((s) => s.user);
  const updateUser = useAuthStore((s) => s.updateUser);
  const [user, setUser] = useState<User | null>(storedUser);
  const [loading, setLoading] = useState(!storedUser);

  useEffect(() => {
    authApi
      .getProfile()
      .then(({ data: apiResponse }) => {
        setUser(apiResponse.data);
        updateUser(apiResponse.data);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [updateUser]);

  if (loading) {
    return <p>Loading...</p>;
  }

  return (
    <div>
      <h2 style={{ fontSize: "20px", fontWeight: 600, marginBottom: "16px" }}>
        Welcome{user ? `, ${user.firstName}` : ""}!
      </h2>
      <p style={{ fontSize: "14px", color: "#6b7280" }}>
        This is your dashboard. Features coming soon.
      </p>
    </div>
  );
}
