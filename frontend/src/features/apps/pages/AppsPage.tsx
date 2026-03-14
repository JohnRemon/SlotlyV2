import { LayoutGrid, Loader2Icon } from "lucide-react";
import { useCallback, useEffect, useRef, useState } from "react";
import { useSearchParams } from "react-router";
import { toast } from "sonner";
import { GoogleCalendarApi } from "../../integrations/google-calendar/api/GoogleCalendarApi";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useApiError } from "@/hooks/useApiError";
import GoogleCalendarCard from "../components/GoogleCalendarCard";

type Tab = "install" | "installed";

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
    const handleError = useApiError();

    useEffect(() => {
        GoogleCalendarApi.getConnectionStatus()
            .then((response) => setIsConnected(response.data.data.status))
            .catch(() => toast.error("Failed to load connection status"))
            .finally(() => setIsStatusLoading(false));
    }, []);

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
                handleError(error);
            } finally {
                setIsActioning(false);
                setSearchParams({ tab: "installed" });
                exchangeInFlightRef.current = false;
            }
        };

        exchange();
    }, [searchParams, setSearchParams, setTab, handleError]);

    const handleConnect = async () => {
        setIsActioning(true);
        try {
            const response = await GoogleCalendarApi.initiateConnection();
            window.location.href = response.data.data.authorizationUrl;
        } catch (error) {
            handleError(error);
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
            handleError(error);
        } finally {
            setIsActioning(false);
        }
    };

    const installedApps = isConnected ? ["google-calendar"] : [];

    return (
        <div className="mx-auto flex w-full max-w-7xl flex-col gap-6">
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

            {isStatusLoading ? (
                <div className="flex justify-center py-16">
                    <Loader2Icon className="size-5 animate-spin text-muted-foreground" />
                </div>
            ) : (
                <>
                    {activeTab === "install" && (
                        <div className="flex flex-col gap-3">
                            {isConnected ? (
                                <EmptyState message="All available apps are installed" />
                            ) : (
                                <GoogleCalendarCard
                                    connected={false}
                                    onConnect={handleConnect}
                                    onDisconnect={handleDisconnect}
                                    isLoading={isActioning}
                                />
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
                                <EmptyState message="No apps installed yet" />
                            )}
                        </div>
                    )}
                </>
            )}
        </div>
    );
};

const EmptyState = ({ message }: { message: string }) => (
    <div className="rounded-2xl border border-dashed bg-card/40 p-10 shadow-sm ring-1 ring-foreground/5 supports-backdrop-filter:backdrop-blur-sm">
        <div className="mx-auto flex max-w-sm flex-col items-center justify-center gap-4 text-center">
            <div className="flex size-14 items-center justify-center rounded-2xl bg-muted/40 ring-1 ring-foreground/10">
                <LayoutGrid className="size-6 text-muted-foreground" />
            </div>
            <p className="text-sm font-semibold tracking-[-0.01em]">
                {message}
            </p>
        </div>
    </div>
);

export default AppsPage;
