import {
    Calendar,
    Clock,
    Link2,
    LogOut,
    Package,
    Settings,
} from "lucide-react";
import * as React from "react";
import { NavLink } from "react-router";

import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

type NavItem = {
    label: string;
    icon: React.ReactNode;
    to: string;
};

type SidebarProps = {
    username?: string;
    avatarChar?: string;
    onLogout?: () => void;
    onNavigate?: () => void;
    variant?: "sidebar" | "drawer";
    className?: string;
};

const NAV_ITEMS: NavItem[] = [
    {
        label: "Events",
        icon: <Link2 className="size-4 shrink-0" />,
        to: "/events",
    },
    {
        label: "Bookings",
        icon: <Calendar className="size-4 shrink-0" />,
        to: "/bookings",
    },
    {
        label: "Schedules",
        icon: <Clock className="size-4 shrink-0" />,
        to: "/schedules",
    },
    {
        label: "Apps",
        icon: <Package className="size-4 shrink-0" />,
        to: "/apps",
    },
];

const navLinkClass = ({ isActive }: { isActive: boolean }) =>
    cn(
        "flex items-center gap-3 rounded-xl px-3 py-2 text-sm font-medium transition-colors",
        "text-sidebar-foreground/70 hover:bg-sidebar-accent hover:text-sidebar-accent-foreground",
        isActive &&
            "bg-sidebar-accent text-sidebar-accent-foreground ring-1 ring-sidebar-border",
    );

const Sidebar = ({
    username = "User",
    avatarChar,
    onLogout,
    onNavigate,
    variant = "sidebar",
    className,
}: SidebarProps) => {
    const initial = avatarChar ?? username[0].toUpperCase();

    return (
        <aside
            className={cn(
                "flex flex-col bg-sidebar text-sidebar-foreground",
                variant === "sidebar"
                    ? "sticky top-0 h-dvh border-r border-sidebar-border"
                    : "h-full",
                className,
            )}
        >
            <div className="flex items-center gap-2 border-b border-sidebar-border px-4 py-4">
                <div className="flex size-8 shrink-0 items-center justify-center rounded-full bg-sidebar-accent text-sidebar-accent-foreground ring-1 ring-sidebar-border">
                    <span className="text-xs font-semibold">{initial}</span>
                </div>
                <span className="text-sm font-semibold">{username}</span>
            </div>

            <nav className="flex-1 px-2 py-3">
                <ul className="flex flex-col gap-1">
                    {NAV_ITEMS.map((item) => (
                        <li key={item.to}>
                            <NavLink
                                to={item.to}
                                onClick={onNavigate}
                                className={navLinkClass}
                            >
                                {item.icon}
                                {item.label}
                            </NavLink>
                        </li>
                    ))}
                </ul>
            </nav>

            <div className="border-t border-sidebar-border p-2">
                <div className="grid gap-1">
                    <NavLink
                        to="/settings"
                        onClick={onNavigate}
                        className={navLinkClass}
                    >
                        <Settings className="size-4 shrink-0" />
                        Settings
                    </NavLink>

                    <Button
                        type="button"
                        variant="ghost"
                        className="justify-start gap-3 rounded-xl text-destructive hover:bg-destructive/10 hover:text-destructive"
                        onClick={() => {
                            onLogout?.();
                            onNavigate?.();
                        }}
                    >
                        <LogOut className="size-4 shrink-0" />
                        Logout
                    </Button>
                </div>
            </div>
        </aside>
    );
};

export default Sidebar;
