import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
    Card,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import {
    CheckIcon,
    ExternalLinkIcon,
    Loader2Icon,
    UnplugIcon,
} from "lucide-react";

const GoogleCalendarIcon = () => (
    <img
        src="https://upload.wikimedia.org/wikipedia/commons/a/a5/Google_Calendar_icon_%282020%29.svg"
        alt="Google Calendar"
        className="w-6.5 h-6.5"
    />
);

interface GoogleCalendarCardProps {
    connected: boolean;
    onConnect: () => void;
    onDisconnect: () => void;
    isLoading: boolean;
}

const GoogleCalendarCard = ({
    connected,
    onConnect,
    onDisconnect,
    isLoading,
}: GoogleCalendarCardProps) => (
    <Card className="bg-card/60">
        <CardHeader className="border-b">
            <div className="flex items-center justify-between gap-4">
                <div className="flex flex-col gap-1.5">
                    <CardTitle className="flex items-center gap-2">
                        <span className="inline-flex size-10 items-center justify-center rounded-xl bg-muted ring-1 ring-border">
                            <GoogleCalendarIcon />
                        </span>
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
                </div>

                {connected ? (
                    <Button
                        type="button"
                        variant="outline"
                        className="shrink-0 text-destructive hover:bg-destructive/10 hover:text-destructive"
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
                        className="shrink-0"
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
            </div>
        </CardHeader>
    </Card>
);

export default GoogleCalendarCard;
