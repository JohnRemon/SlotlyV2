import { Calendar, ChevronDown, Clock, Link2, Search } from "lucide-react";

interface SidebarProps {
    username?: string;
    avatarChar?: string;
    activeLink?: "Event Types" | "Bookings" | "Availability";
}

const navLinks: { label: "Event Types" | "Bookings" | "Availability"; icon: React.ReactNode }[] = [
    { label: "Event Types", icon: <Link2 className="w-5 h-5" /> },
    { label: "Bookings", icon: <Calendar className="w-5 h-5" /> },
    { label: "Availability", icon: <Clock className="w-5 h-5" /> },
];

export const Sidebar = ({ username = "User", avatarChar = "U", activeLink = "Event Types" }: SidebarProps) => {
    return (
        <aside className="h-screen w-56 bg-base-200 flex flex-col">
            <div className="p-4 border-b border-base-300 flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-primary text-primary-content flex items-center justify-center font-bold">
                    {avatarChar}
                </div>
                <span className="font-medium truncate flex-1">{username}</span>
                <button className="btn btn-ghost btn-xs btn-circle">
                    <Search className="w-4 h-4" />
                </button>
                <button className="btn btn-ghost btn-xs btn-circle">
                    <ChevronDown className="w-4 h-4" />
                </button>
            </div>
            <nav className="flex-1 p-2">
                <ul className="menu gap-1">
                    {navLinks.map(({ label, icon }) => (
                        <li key={label}>
                            <a
                                href={`/${label.toLowerCase().replace(" ", "-")}`}
                                className={activeLink === label ? "bg-primary text-primary-content" : ""}
                            >
                                {icon}
                                {label}
                            </a>
                        </li>
                    ))}
                </ul>
            </nav>
        </aside>
    );
};
