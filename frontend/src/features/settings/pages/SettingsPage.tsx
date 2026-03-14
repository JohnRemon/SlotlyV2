import { cn } from "@/lib/utils";
import { UserIcon } from "lucide-react";
import { useSearchParams } from "react-router";
import ProfileTab from "./ProfileTab";

type Tab = "profile";

const TABS: { id: Tab; label: string; icon: React.ReactNode }[] = [
    {
        id: "profile",
        label: "Profile",
        icon: <UserIcon className="w-4 h-4" />,
    },
];

const SettingsPage = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const activeTab = (searchParams.get("tab") as Tab) ?? "profile";
    const setActiveTab = (tab: Tab) => setSearchParams({ tab });

    return (
        <div className="mx-auto flex w-full max-w-7xl flex-col gap-6">
            <div>
                <h1 className="text-xl font-semibold tracking-[-0.02em]">
                    Settings
                </h1>
            </div>

            <div className="grid gap-6 lg:grid-cols-[14rem_1fr]">
                <nav className="flex gap-1 overflow-x-auto lg:flex-col lg:overflow-x-visible">
                    {TABS.map((tab) => (
                        <button
                            key={tab.id}
                            type="button"
                            onClick={() => setActiveTab(tab.id)}
                            className={cn(
                                "flex items-center gap-2.5 rounded-xl px-3 py-2 text-sm font-medium transition-colors whitespace-nowrap",
                                "text-muted-foreground hover:bg-muted/50 hover:text-foreground",
                                activeTab === tab.id &&
                                    "bg-muted text-foreground ring-1 ring-foreground/10",
                            )}
                        >
                            {tab.icon}
                            {tab.label}
                        </button>
                    ))}
                </nav>

                <div>{activeTab === "profile" && <ProfileTab />}</div>
            </div>
        </div>
    );
};

export default SettingsPage;
