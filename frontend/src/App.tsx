import { Route, Routes } from "react-router";
import ProtectedRoute from "./components/routing/ProtectedRoute.tsx";
import LoginPage from "./features/auth/pages/LoginPage.tsx";
import AvailabilityPage from "./pages/AvailabilityPage.tsx";
import DashboardPage from "./pages/DashboardPage.tsx";
import BookingsPage from "./features/bookings/pages/BookingsPage.tsx";
import ProfilePage from "./pages/ProfilePage.tsx";
import SchedulingPage from "./pages/SchedulingPage.tsx";
import SettingsPage from "./pages/SettingsPage.tsx";
import AppsPage from "./pages/AppsPage.tsx";
import HomePage from "./pages/HomePage.tsx";

function App() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/" element={<HomePage />} />

            <Route element={<ProtectedRoute />}>
                <Route element={<DashboardPage />}>
                    <Route path="/scheduling" element={<SchedulingPage />} />
                    <Route path="/bookings" element={<BookingsPage />} />
                    <Route
                        path="/availability"
                        element={<AvailabilityPage />}
                    />
                    <Route path="/apps" element={<AppsPage />} />
                    <Route path="/me" element={<ProfilePage />} />
                    <Route path="/settings" element={<SettingsPage />} />
                </Route>
            </Route>
        </Routes>
    );
}

export default App;
