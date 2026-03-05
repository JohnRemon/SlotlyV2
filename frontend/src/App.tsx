import { Route, Routes } from "react-router";
import ProtectedRoute from "./components/protected-route.tsx";
import LoginPage from "./pages/auth/login-page.tsx";
import AvailabilityPage from "./pages/availability-page.tsx";
import DashBoard from "./pages/dashboard-page.tsx";
import MeetingsPage from "./pages/meetings-page";
import ProfilePage from "./pages/profile-page.tsx";
import SchedulingPage from "./pages/scheduling-page.tsx";
import SettingsPage from "./pages/settings-page.tsx";

function App() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route element={<ProtectedRoute />}>
                <Route element={<DashBoard />}>
                    <Route path="/scheduling" element={<SchedulingPage />} />
                    <Route path="/meetings" element={<MeetingsPage />} />
                    <Route
                        path="/availability"
                        element={<AvailabilityPage />}
                    />
                    <Route path="/me" element={<ProfilePage />} />
                    <Route path="/settings" element={<SettingsPage />} />
                </Route>
            </Route>
        </Routes>
    );
}

export default App;
