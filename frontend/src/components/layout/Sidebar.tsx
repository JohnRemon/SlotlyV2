import {
    Calendar,
    Clock,
    Link2,
    LogOut,
    Package,
    Settings,
    User,
} from "lucide-react";

// ─── Types ───────────────────────────────────────────────────────────────────

type ActiveLink = "Events" | "Bookings" | "Schedules" | "Apps";

interface NavLink {
    label: ActiveLink;
    icon: React.ReactNode;
    href: string;
}

interface SidebarProps {
    username?: string;
    avatarChar?: string;
    activeLink?: ActiveLink;
    onLogout?: () => void;
}

// ─── Constants ───────────────────────────────────────────────────────────────

const NAV_LINKS: NavLink[] = [
    {
        label: "Events",
        icon: <Link2 className="w-4 h-4 shrink-0" />,
        href: "/events",
    },
    {
        label: "Bookings",
        icon: <Calendar className="w-4 h-4 shrink-0" />,
        href: "/bookings",
    },
    {
        label: "Schedules",
        icon: <Clock className="w-4 h-4 shrink-0" />,
        href: "/schedules",
    },
    {
        label: "Apps",
        icon: <Package className="w-4 h-4 shrink-0" />,
        href: "/apps",
    },
];

// ─── Component ───────────────────────────────────────────────────────────────

const Sidebar = ({
    username = "User",
    avatarChar,
    activeLink = "Events",
    onLogout,
}: SidebarProps) => {
    const initial = avatarChar ?? username[0].toUpperCase();

    return (
        <aside className="h-screen w-60 bg-base-100 border-r border-base-300 flex flex-col">
            {/* ── Profile Header ── */}
            <div className="flex items-center gap-3 px-4 py-4 border-b border-base-300">
                <div className="w-8 h-8 rounded-full bg-primary text-primary-content flex items-center justify-center font-bold text-sm shrink-0">
                    {initial}
                </div>
                <span className="font-semibold text-sm truncate flex-1 text-base-content">
                    {username}
                </span>
            </div>

            {/* ── Nav Links ── */}
            <nav className="flex-1 px-2 py-3">
                <ul className="flex flex-col gap-0.5">
                    {NAV_LINKS.map(({ label, icon, href }) => {
                        const isActive = activeLink === label;
                        return (
                            <li key={label}>
                                <a
                                    href={href}
                                    className={`flex items-center gap-3 w-full px-3 py-2 rounded-lg text-sm font-medium transition-colors 
                                    ${
                                        isActive
                                            ? "bg-base-200 text-base-content"
                                            : "text-base-content/60 hover:bg-base-200 hover:text-base-content"
                                    }`}
                                >
                                    {icon}
                                    {label}
                                </a>
                            </li>
                        );
                    })}
                </ul>
            </nav>

            {/* ── Footer Actions ── */}
            <div className="px-2 py-3 border-t border-base-300 flex flex-col gap-0.5">
                <a
                    href="/me"
                    className="flex items-center gap-3 w-full px-3 py-2 rounded-lg text-sm font-medium text-base-content/60 hover:bg-base-200 hover:text-base-content transition-colors"
                >
                    <User className="w-4 h-4 shrink-0" />
                    Profile
                </a>
                <a
                    href="/settings"
                    className="flex items-center gap-3 w-full px-3 py-2 rounded-lg text-sm font-medium text-base-content/60 hover:bg-base-200 hover:text-base-content transition-colors"
                >
                    <Settings className="w-4 h-4 shrink-0" />
                    Settings
                </a>
                <button
                    onClick={onLogout}
                    className="flex items-center gap-3 w-full px-3 py-2 rounded-lg text-sm font-medium text-error/70 hover:bg-error/10 hover:text-error transition-colors cursor-pointer"
                >
                    <LogOut className="w-4 h-4 shrink-0" />
                    Logout
                </button>
            </div>
        </aside>
    );
};

export default Sidebar;
