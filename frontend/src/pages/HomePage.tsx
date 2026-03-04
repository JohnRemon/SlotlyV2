import { type ReactNode } from "react";
import { Sidebar } from "../components/sidebar";

interface HomePageProps {
    children?: ReactNode;
}

export const HomePage = ({ children }: HomePageProps) => {
    return (
        <div className="flex h-screen bg-base-200">
            <Sidebar
                username="John Remon"
                activeLink="Event Types"
                onLogout={() => console.log("logout")}
            />
            <main className="flex-1 overflow-y-auto p-6">{children}</main>
        </div>
    );
};
