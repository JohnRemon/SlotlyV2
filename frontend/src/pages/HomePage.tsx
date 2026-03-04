import { type ReactNode } from "react";
import { Sidebar } from "../components/sidebar";

interface HomePageProps {
    children?: ReactNode;
}

export const HomePage = ({ children }: HomePageProps) => {
    return (
        <div className="flex h-screen">
            <Sidebar username="John" avatarChar="J" />
            <div className="flex flex-col flex-1 overflow-hidden">
                <main className="flex-1 overflow-y-auto p-6 bg-base-200">
                    {children}
                </main>
            </div>
        </div>
    );
};
