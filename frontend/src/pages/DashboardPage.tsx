import { Outlet, useNavigate } from "react-router";

import { toast } from "sonner";
import Sidebar from "../components/common/Sidebar";
import { useAuth } from "../features/auth/hooks/useAuth";

import { AuthApi } from "@/features/auth/api/AuthApi";

const DashboardPage = () => {
    const { user } = useAuth();
    const navigate = useNavigate();

    const username = user
        ? `${user.firstName} ${user.lastName}`
        : "Slotly user";
    const avatarChar = user?.firstName?.[0]?.toUpperCase() ?? "S";

    const handleLogout = () => {
        AuthApi.logout();
        navigate("/");
        toast.success("Successfully signed out");
    };

    return (
        <div className="min-h-dvh bg-linear-to-b from-background to-muted/30">
            <div className="grid min-h-dvh w-full grid-cols-1 md:grid-cols-[18rem_1fr]">
                <Sidebar
                    className="hidden md:flex"
                    username={username}
                    avatarChar={avatarChar}
                    onLogout={handleLogout}
                />

                <div className="flex min-w-0 flex-col">
                    <main className="min-w-0 flex-1 px-4 py-6 md:px-6 md:py-8">
                        <Outlet />
                    </main>
                </div>
            </div>
        </div>
    );
};
export default DashboardPage;
