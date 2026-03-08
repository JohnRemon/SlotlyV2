import axios from "axios";
import { Check, ExternalLink, Unplug } from "lucide-react";
import { useCallback, useEffect, useRef, useState } from "react";
import toast from "react-hot-toast";
import { useSearchParams } from "react-router";
import {
    disconnectCalendar,
    exchangeAuthorizationCode,
    getConnectionStatus,
    initiateConnection,
} from "../../integrations/google-calendar/api/GoogleCalendarApi";

type Tab = "install" | "installed";

// ── Google Calendar SVG icon ──────────────────────────────────────────────────
const GoogleCalendarIcon = () => (
    <img
        src="https://upload.wikimedia.org/wikipedia/commons/a/a5/Google_Calendar_icon_%282020%29.svg"
        alt="Google Calendar"
        className="w-8 h-8"
    />
);

interface GoogleCalendarCardProps {
    connected: boolean;
    onConnect: () => void;
    onDisconnect: () => void;
    isLoading: boolean;
}

// ── App card ──────────────────────────────────────────────────────────────────
const GoogleCalendarCard = ({
    connected,
    onConnect,
    onDisconnect,
    isLoading,
}: GoogleCalendarCardProps) => (
    <div className="flex items-start justify-between p-5 border border-base-300 rounded-xl">
        <div className="flex items-start gap-4">
            <div className="w-12 h-12 rounded-xl bg-base-200 flex items-center justify-center shrink-0">
                <GoogleCalendarIcon />
            </div>
            <div>
                <div className="flex items-center gap-2">
                    <span className="text-sm font-semibold flex items-center gap-1">
                        Google Calendar
                        <a
                            href="https://calendar.google.com"
                            target="_blank"
                            rel="noreferrer"
                            className="text-xs text-primary hover:underline"
                        >
                            <ExternalLink className="w-3.5 h-3.5" />
                        </a>
                    </span>
                    {connected && (
                        <span className="flex items-center gap-1 text-xs bg-success/10 text-success px-1.5 py-0.5 rounded-md font-medium">
                            <Check className="w-3 h-3" />
                            Connected
                        </span>
                    )}
                </div>
                <p className="text-xs text-base-content/50 mt-1 max-w-sm">
                    Sync your bookings with Google Calendar and block time
                    automatically based on your calendar events.
                </p>
            </div>
        </div>

        <div className="shrink-0 ml-4">
            {connected ? (
                <button
                    type="button"
                    className="btn btn-outline btn-sm gap-1.5 text-error border-error hover:bg-error hover:text-white"
                    disabled={isLoading}
                    onClick={onDisconnect}
                >
                    {isLoading ? (
                        <span className="loading loading-spinner loading-xs" />
                    ) : (
                        <Unplug className="w-3.5 h-3.5" />
                    )}
                    Disconnect
                </button>
            ) : (
                <button
                    type="button"
                    className="btn btn-primary btn-sm"
                    disabled={isLoading}
                    onClick={onConnect}
                >
                    {isLoading ? (
                        <span className="loading loading-spinner loading-xs" />
                    ) : (
                        "Connect"
                    )}
                </button>
            )}
        </div>
    </div>
);

// ── AppsPage ──────────────────────────────────────────────────────────────────
const AppsPage = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const activeTab = (searchParams.get("tab") as Tab) ?? "install";
    const setTab = useCallback(
        (tab: Tab) => setSearchParams({ tab }),
        [setSearchParams],
    );

    const [isConnected, setIsConnected] = useState(false);
    const [isStatusLoading, setIsStatusLoading] = useState(true);
    const [isActioning, setIsActioning] = useState(false);
    const exchangeInFlightRef = useRef(false);

    useEffect(() => {
        getConnectionStatus()
            .then(setIsConnected)
            .catch(() => toast.error("Failed to load connection status"))
            .finally(() => setIsStatusLoading(false));
    }, []);

    // Handle OAuth callback — code + state come back as query params
    useEffect(() => {
        const code = searchParams.get("code");
        const state = searchParams.get("state");
        if (!code || !state) return;

        const exchangeKey = `google-oauth-exchange:${code}:${state}`;
        if (
            exchangeInFlightRef.current ||
            sessionStorage.getItem(exchangeKey) === "done"
        ) {
            setSearchParams({ tab: "installed" });
            return;
        }

        const exchange = async () => {
            exchangeInFlightRef.current = true;
            sessionStorage.setItem(exchangeKey, "done");
            setIsActioning(true);
            setSearchParams({ tab: "installed" });
            try {
                await exchangeAuthorizationCode(code, state);
                setIsConnected(true);
                setTab("installed");
                toast.success("Google Calendar connected");
            } catch (error) {
                if (axios.isAxiosError(error)) {
                    toast.error(
                        error.response?.data?.message ?? "Failed to connect",
                    );
                } else {
                    toast.error("Something went wrong");
                }
            } finally {
                setIsActioning(false);
                setSearchParams({ tab: "installed" });
                exchangeInFlightRef.current = false;
            }
        };

        exchange();
    }, [searchParams, setSearchParams, setTab]);

    const handleConnect = async () => {
        setIsActioning(true);
        try {
            const url = await initiateConnection();
            window.location.href = url;
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(
                    error.response?.data?.message ??
                        "Failed to initiate connection",
                );
            } else {
                toast.error("Something went wrong");
            }
            setIsActioning(false);
        }
    };

    const handleDisconnect = async () => {
        setIsActioning(true);
        try {
            await disconnectCalendar();
            setIsConnected(false);
            setTab("install");
            toast.success("Google Calendar disconnected");
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(
                    error.response?.data?.message ?? "Failed to disconnect",
                );
            } else {
                toast.error("Something went wrong");
            }
        } finally {
            setIsActioning(false);
        }
    };

    const installedApps = isConnected ? ["google-calendar"] : [];

    return (
        <div className="p-6 max-w-3xl mx-auto">
            <div className="mb-6">
                <h1 className="text-lg font-bold text-base-content">Apps</h1>
                <p className="text-xs text-base-content/40 mt-0.5">
                    Connect your favourite tools to extend Slotly
                </p>
            </div>

            {/* Tabs */}
            <div className="flex gap-1 mb-6 border-b border-base-300">
                {(["install", "installed"] as Tab[]).map((tab) => (
                    <button
                        key={tab}
                        type="button"
                        onClick={() => setTab(tab)}
                        className={`px-4 py-2 text-sm font-medium capitalize border-b-2 -mb-px transition-colors
                            ${
                                activeTab === tab
                                    ? "border-primary text-base-content"
                                    : "border-transparent text-base-content/40 hover:text-base-content"
                            }`}
                    >
                        {tab}
                        {tab === "installed" && installedApps.length > 0 && (
                            <span className="ml-1.5 text-xs bg-primary/10 text-primary px-1.5 py-0.5 rounded-full font-medium">
                                {installedApps.length}
                            </span>
                        )}
                    </button>
                ))}
            </div>

            {/* Content */}
            {isStatusLoading ? (
                <div className="flex items-center justify-center h-40">
                    <span className="loading loading-spinner loading-md text-primary" />
                </div>
            ) : (
                <>
                    {activeTab === "install" && (
                        <div className="flex flex-col gap-3">
                            {!isConnected && (
                                <GoogleCalendarCard
                                    connected={false}
                                    onConnect={handleConnect}
                                    onDisconnect={handleDisconnect}
                                    isLoading={isActioning}
                                />
                            )}
                            {isConnected && (
                                <div className="flex flex-col items-center justify-center py-16 gap-2 text-center border border-dashed border-base-300 rounded-xl">
                                    <p className="text-sm text-base-content/40">
                                        All available apps are installed
                                    </p>
                                </div>
                            )}
                        </div>
                    )}

                    {activeTab === "installed" && (
                        <div className="flex flex-col gap-3">
                            {isConnected ? (
                                <GoogleCalendarCard
                                    connected={true}
                                    onConnect={handleConnect}
                                    onDisconnect={handleDisconnect}
                                    isLoading={isActioning}
                                />
                            ) : (
                                <div className="flex flex-col items-center justify-center py-16 gap-2 text-center border border-dashed border-base-300 rounded-xl">
                                    <p className="text-sm text-base-content/40">
                                        No apps installed yet
                                    </p>
                                </div>
                            )}
                        </div>
                    )}
                </>
            )}
        </div>
    );
};

export default AppsPage;
