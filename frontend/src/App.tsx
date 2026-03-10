import { Route, Routes } from "react-router";
import AppsPage from "./features/apps/pages/AppsPage";
import ForgotPasswordPage from "./features/auth/pages/ForgotPasswordPage";
import LoginPage from "./features/auth/pages/LoginPage";
import RegisterPage from "./features/auth/pages/RegisterPage";
import ResetPasswordPage from "./features/auth/pages/ResetPasswordPage";
import BookingSlotsPage from "./features/booking-page/pages/BookingSlotsPage";
import BookingsPage from "./features/bookings/pages/BookingsPage";
import EventDetailPage from "./features/events/pages/EventDetailsPage";
import EventsPage from "./features/events/pages/EventsPage";
import ProtectedRoute from "./components/common/ProtectedRoute";
import SchedulesProvider from "./features/schedule/context/SchedulesContext";
import ScheduleDetailPage from "./features/schedule/pages/ScheduleDetailPage";
import SchedulesPage from "./features/schedule/pages/SchedulesPage";
import HomePage from "./pages/HomePage";
import DashboardPage from "./pages/DashboardPage";

function App() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/forgot-password" element={<ForgotPasswordPage />} />
            <Route
                path="/forgot-password/:token"
                element={<ResetPasswordPage />}
            />
            <Route path="/book/:shareableId" element={<BookingSlotsPage />} />
            <Route path="/" element={<HomePage />} />

            <Route element={<ProtectedRoute />}>
                <Route element={<DashboardPage />}>
                    <Route path="/events" element={<EventsPage />} />
                    <Route path="/events/:id" element={<EventDetailPage />} />
                    <Route path="/bookings" element={<BookingsPage />} />
                    <Route element={<SchedulesProvider />}>
                        <Route path="/schedules" element={<SchedulesPage />} />
                        <Route
                            path="/schedules/:id"
                            element={<ScheduleDetailPage />}
                        />
                    </Route>
                    <Route path="/apps" element={<AppsPage />} />
                    {/* <Route path="/me" element={<ProfilePage />} /> */}
                    {/* <Route path="/settings" element={<SettingsPage />} /> */}
                </Route>
            </Route>
        </Routes>
    );
}

export default App;
