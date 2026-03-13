import { BrowserRouter, Route, Routes } from "react-router";
import ProtectedRoute from "./components/common/ProtectedRoute";
import AppsPage from "./features/apps/pages/AppsPage";
import ForgotPasswordPage from "./features/auth/pages/ForgotPasswordPage";
import LoginPage from "./features/auth/pages/LoginPage";
import RegisterPage from "./features/auth/pages/RegisterPage";
import ResetPasswordPage from "./features/auth/pages/ResetPasswordPage";
import BookingsPage from "./features/bookings/pages/BookingsPage";
import EventDetailPage from "./features/events/pages/EventDetailsPage";
import EventsPage from "./features/events/pages/EventsPage";
import SchedulesProvider from "./features/schedule/context/SchedulesContext";
import ScheduleDetailPage from "./features/schedule/pages/ScheduleDetailPage";
import SchedulesPage from "./features/schedule/pages/SchedulesPage";
import DashboardPage from "./pages/DashboardPage";
import HomePage from "./pages/HomePage";
import { GoogleOAuthProvider } from "@react-oauth/google";
import { AuthProvider } from "./features/auth/providers/AuthProvider";
import { Toaster } from "./components/ui/sonner";
import SlotsPage from "./features/booking-page/pages/SlotsPage";
import ThemeToggle from "./components/common/ThemeToggle";
import { ThemeProvider } from "next-themes";

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;

function App() {
    return (
        <BrowserRouter>
            <GoogleOAuthProvider clientId={GOOGLE_CLIENT_ID}>
                <ThemeProvider
                    attribute="class"
                    defaultTheme="system"
                    enableSystem
                    disableTransitionOnChange
                >
                    <AuthProvider>
                        <ThemeToggle />
                        <Toaster />
                        <Routes>
                            <Route path="/login" element={<LoginPage />} />
                            <Route path="/register" element={<RegisterPage />} />
                            <Route
                                path="/forgot-password"
                                element={<ForgotPasswordPage />}
                            />
                            <Route
                                path="/forgot-password/:token"
                                element={<ResetPasswordPage />}
                            />
                            <Route
                                path="/book/:shareableId"
                                element={<SlotsPage />}
                            />
                            <Route path="/" element={<HomePage />} />

                            <Route element={<ProtectedRoute />}>
                                <Route element={<DashboardPage />}>
                                    <Route
                                        path="/events"
                                        element={<EventsPage />}
                                    />
                                    <Route
                                        path="/events/:id"
                                        element={<EventDetailPage />}
                                    />
                                    <Route
                                        path="/bookings"
                                        element={<BookingsPage />}
                                    />
                                    <Route element={<SchedulesProvider />}>
                                        <Route
                                            path="/schedules"
                                            element={<SchedulesPage />}
                                        />
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
                    </AuthProvider>
                </ThemeProvider>
            </GoogleOAuthProvider>
        </BrowserRouter>
    );
}

export default App;
