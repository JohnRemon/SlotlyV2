import { Clock, Copy, ExternalLink, Trash } from "lucide-react";
import type React from "react";
import { useNavigate } from "react-router";
import { toast } from "sonner";
import type { EventResponse } from "../types/Event";

import { Badge } from "@/components/ui/badge";
import { buttonVariants } from "@/components/ui/button";
import { Switch } from "@/components/ui/switch";
import { cn } from "@/lib/utils";

interface EventCardProps {
    event: EventResponse;
    onDelete: (event: EventResponse) => void;
    onToggleVisibility: (id: number, isPublic: boolean) => Promise<void>;
}

export const EventCard = ({
    event,
    onDelete,
    onToggleVisibility,
}: EventCardProps) => {
    const navigate = useNavigate();
    const bookingUrl = `${window.location.origin}/book/${event.shareableId}`;
    const rules = event.availabilityRules ?? {};
    const isPublic = rules.isPublic ?? false;

    const handleCopy = async (e: React.MouseEvent) => {
        e.stopPropagation();
        await navigator.clipboard.writeText(bookingUrl);
        toast.success("Link copied!");
    };

    const handleToggle = async (next: boolean) => {
        await onToggleVisibility(event.id, next);
    };

    return (
        <div
            role="button"
            tabIndex={0}
            className={cn(
                "flex items-center gap-4 rounded-2xl border bg-card/60 px-4 py-3 shadow-sm ring-1 ring-foreground/5 transition-colors",
                "cursor-pointer hover:bg-card/80 hover:ring-foreground/10",
                "focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-ring/40",
            )}
            onClick={() => navigate(`/events/${event.id}`)}
            onKeyDown={(e) => {
                if (e.key === "Enter" || e.key === " ") {
                    e.preventDefault();
                    navigate(`/events/${event.id}`);
                }
            }}
        >
            <div className="min-w-0 flex-1">
                <div className="truncate text-sm font-semibold tracking-[-0.01em]">
                    {event.eventName}
                </div>
                <div className="mt-1 flex items-center gap-2">
                    <Badge variant="outline">
                        <Clock className="size-3.5" />
                        {rules.slotDurationMinutes ?? "—"} min
                    </Badge>
                </div>
            </div>

            <div
                className="flex items-center gap-2"
                onClick={(e) => e.stopPropagation()}
                onKeyDown={(e) => e.stopPropagation()}
            >
                <Badge variant={isPublic ? "secondary" : "outline"}>
                    {isPublic ? "Public" : "Private"}
                </Badge>
                <Switch
                    checked={isPublic}
                    aria-label="Toggle visibility"
                    onCheckedChange={(next) => {
                        void handleToggle(next);
                    }}
                />
            </div>

            <div
                className="hidden items-center gap-1 sm:flex"
                onClick={(e) => e.stopPropagation()}
                onKeyDown={(e) => e.stopPropagation()}
            >
                <button
                    type="button"
                    className={buttonVariants({
                        variant: "ghost",
                        size: "icon-sm",
                    })}
                    onClick={handleCopy}
                    title="Copy booking link"
                    aria-label="Copy booking link"
                >
                    <Copy className="size-4" />
                </button>
                <a
                    href={`/book/${event.shareableId}`}
                    className={buttonVariants({
                        variant: "ghost",
                        size: "icon-sm",
                    })}
                    title="View booking page"
                    aria-label="View booking page"
                    target="_blank"
                    rel="noreferrer"
                >
                    <ExternalLink className="size-4" />
                </a>
                <button
                    type="button"
                    className={cn(
                        buttonVariants({ variant: "ghost", size: "icon-sm" }),
                        "text-destructive hover:bg-destructive/10 hover:text-destructive",
                    )}
                    onClick={() => onDelete(event)}
                    title="Delete event"
                    aria-label="Delete event"
                >
                    <Trash className="size-4" />
                </button>
            </div>
        </div>
    );
};
