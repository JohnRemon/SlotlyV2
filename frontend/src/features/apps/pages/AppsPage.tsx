import axios from "axios";
import {
    CheckIcon,
    ExternalLinkIcon,
    Loader2Icon,
    UnplugIcon,
} from "lucide-react";
import { useCallback, useEffect, useRef, useState } from "react";
import { toast } from "sonner";
import { useSearchParams } from "react-router";
import { GoogleCalendarApi } from "../../integrations/google-calendar/api/GoogleCalendarApi";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
    Card,
    CardAction,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";

type Tab = "install" | "installed";

// -- Google Calendar SVG icon --------------------------------------------------
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

// -- App card ------------------------------------------------------------------
const GoogleCalendarCard = ({
    connected,
    onConnect,
    onDisconnect,
    isLoading,
}: GoogleCalendarCardProps) => (
    <Card className="bg-card/60">
        <CardHeader className="border-b">
            <CardTitle className="flex items-center gap-2">
                <span className="inline-flex size-10 items-center justify-center rounded-xl bg-muted ring-1 ring-border">
                    <GoogleCalendarIcon />
                </span>
                <span className="flex items-center gap-2">
                    Google Calendar
                    <a
                        href="https://calendar.google.com"
                        target="_blank"
                        rel="noreferrer"
                        className="inline-flex items-center text-muted-foreground hover:text-foreground"
                        aria-label="Open Google Calendar"
                    >
                        <ExternalLinkIcon className="size-4" />
                    </a>
                </span>
                {connected && (
                    <Badge variant="secondary" className="gap-1">
                        <CheckIcon className="size-3.5" />
                        Connected
                    </Badge>
                )}
            </CardTitle>
            <CardDescription>
                Sync your bookings with Google Calendar and block time
                automatically based on your calendar events.
            </CardDescription>
            <CardAction>
                {connected ? (
                    <Button
                        type="button"
                        variant="outline"
                        className="text-destructive hover:bg-destructive/10 hover:text-destructive"
                        disabled={isLoading}
                        onClick={onDisconnect}
                    >
                        {isLoading ? (
                            <Loader2Icon className="size-4 animate-spin" />
                        ) : (
                            <UnplugIcon className="size-4" />
                        )}
                        Disconnect
                    </Button>
                ) : (
                    <Button
                        type="button"
                        disabled={isLoading}
                        onClick={onConnect}
                    >
                        {isLoading ? (
                            <Loader2Icon className="size-4 animate-spin" />
                        ) : (
                            "Connect"
                        )}
                    </Button>
                )}
            </CardAction>
        </CardHeader>
    </Card>
);

// -- AppsPage ------------------------------------------------------------------
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
        GoogleCalendarApi.getConnectionStatus()
            .then((response) => setIsConnected(response.data.data.status))
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
                await GoogleCalendarApi.exchangeAuthorizationCode({
                    code,
                    state,
                });
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
            const response = await GoogleCalendarApi.initiateConnection();
            window.location.href = response.data.data.authorizationUrl;
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
            await GoogleCalendarApi.disconnectCalendar();
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
        <div className="mx-auto flex w-full max-w-7xl flex-col gap-6 px-4 py-8">
            <div>
                <h1 className="text-base font-semibold tracking-[-0.01em]">
                    Apps
                </h1>
                <p className="mt-1 text-xs text-muted-foreground">
                    Connect your favourite tools to extend Slotly
                </p>
            </div>

            {/* Tabs */}
            <div className="inline-flex w-fit items-center gap-1 rounded-xl bg-muted/40 p-1 ring-1 ring-border">
                {(["install", "installed"] as Tab[]).map((tab) => (
                    <Button
                        key={tab}
                        type="button"
                        onClick={() => setTab(tab)}
                        size="sm"
                        variant={activeTab === tab ? "secondary" : "ghost"}
                        className="h-8 gap-2 rounded-lg px-3 capitalize"
                    >
                        {tab}
                        {tab === "installed" && installedApps.length > 0 && (
                            <Badge variant="outline" className="ml-0.5">
                                {installedApps.length}
                            </Badge>
                        )}
                    </Button>
                ))}
            </div>

            {/* Content */}
            {isStatusLoading ? (
                <div className="flex items-center justify-center h-40">
                    <Loader2Icon className="size-5 animate-spin text-muted-foreground" />
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
                                <div className="flex flex-col items-center justify-center gap-2 rounded-xl border border-dashed border-border bg-card/20 p-10 text-center">
                                    <p className="text-sm text-muted-foreground">
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
                                <div className="flex flex-col items-center justify-center gap-2 rounded-xl border border-dashed border-border bg-card/20 p-10 text-center">
                                    <p className="text-sm text-muted-foreground">
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
