import { Outlet } from "react-router-dom";

export function AuthLayout() {
  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        minHeight: "100vh",
        padding: "20px",
      }}
    >
      <Outlet />
    </div>
  );
}
