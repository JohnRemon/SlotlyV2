import { Outlet, useNavigate } from "react-router";
import Sidebar from "../components/sidebar";
import { useAuth } from "../hooks/useAuth";
import { logout } from "../lib/auth";
import toast from "react-hot-toast";

const DashBoard = () => {
    const { user } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/");
        toast.success("Successfully signed out");
    };

    return (
        <div className="flex h-screen bg-base-200">
            <Sidebar
                username={`${user?.firstName} ${user?.lastName}`}
                avatarChar={user?.firstName?.[0]}
                activeLink="Scheduling"
                onLogout={handleLogout}
            />
            <main className="flex-1 overflow-y-auto p-6">
                <Outlet />
            </main>
        </div>
    );
};

export default DashBoard;
