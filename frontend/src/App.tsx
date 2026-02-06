import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { GuestRoute } from "@/components/auth/GuestRoute";
import { ProtectedRoute } from "@/components/auth/ProtectedRoute";
import { AuthLayout } from "@/components/layouts/AuthLayout";
import { DashboardLayout } from "@/components/layouts/DashboardLayout";
import { LoginPage } from "@/pages/HomePage";
import { RegisterPage } from "@/pages/RegisterPage";
import { VerifyEmailPage } from "@/pages/VerifyEmailPage";
import { DashboardPage } from "@/pages/DashboardPage";
import { OAuthCallback } from "@/pages/OAuthCallback";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<GuestRoute />}>
          <Route element={<AuthLayout />}>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/verify-email" element={<VerifyEmailPage />} />
          </Route>
        </Route>

        <Route path="/oauth/callback" element={<OAuthCallback />} />

        <Route element={<ProtectedRoute />}>
          <Route element={<DashboardLayout />}>
            <Route path="/dashboard" element={<DashboardPage />} />
          </Route>
        </Route>

        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
