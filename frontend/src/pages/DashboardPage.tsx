import { Outlet, useNavigate, useLocation } from "react-router";
import Sidebar from "../components/layout/Sidebar";
import { useAuth } from "../features/auth/hooks/useAuth";
import { logout } from "../features/auth/api/AuthApi";
import toast from "react-hot-toast";

type ActiveLink = "Scheduling" | "Bookings" | "Availability" | "Apps";

const DashboardPage = () => {
    const { user } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        logout();
        navigate("/");
        toast.success("Successfully signed out");
    };

    const getActiveLink = (pathname: string): ActiveLink => {
        if (pathname.startsWith("/scheduling")) return "Scheduling";
        if (pathname.startsWith("/bookings")) return "Bookings";
        if (pathname.startsWith("/availability")) return "Availability";
        if (pathname.startsWith("/apps")) return "Apps";
        return "Scheduling";
    };

    return (
        <div className="flex h-screen bg-base-200">
            <Sidebar
                username={`${user?.firstName} ${user?.lastName}`}
                avatarChar={user?.firstName?.[0]}
                activeLink={getActiveLink(location.pathname)}
                onLogout={handleLogout}
            />
            <main className="flex-1 overflow-y-auto p-6">
                <Outlet />
            </main>
        </div>
    );
};
export default DashboardPage;
