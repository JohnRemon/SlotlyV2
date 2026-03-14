import type { PublicEventResponse } from "@/features/events/types/Event";
import { Clock, Globe, FileText } from "lucide-react";

interface EventPanelProps {
    event: PublicEventResponse;
    selectedSlot?: string;
}

export const EventPanel = ({ event, selectedSlot }: EventPanelProps) => {
    const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
    const firstName = event.host.firstName;
    const lastName = event.host.lastName;
    const initial = firstName[0].toUpperCase();

    return (
        <div className="flex flex-col gap-5">
            {/* Host */}
            <div className="flex items-center gap-2">
                <div className="flex size-8 shrink-0 items-center justify-center rounded-full bg-primary text-primary-foreground ring-1 ring-primary/20">
                    {initial}
                </div>
                <span className="text-sm text-muted-foreground">
                    {firstName} {lastName}
                </span>
            </div>

            {/* Event Name */}
            <div>
                <h1 className="text-2xl font-bold leading-tight tracking-[-0.02em]">
                    {event.eventName}
                </h1>
            </div>

            {/* Meta */}
            <div className="flex flex-col gap-2.5">
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                    <Clock className="size-4 shrink-0" />
                    {event.slotDurationMinutes} min
                </div>
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                    <Globe className="size-4 shrink-0" />
                    {timezone}
                </div>
                {event.description && (
                    <div className="flex items-start gap-2 text-sm text-muted-foreground">
                        <FileText className="mt-0.5 size-4 shrink-0" />
                        <p>{event.description}</p>
                    </div>
                )}
            </div>

            {/* Selected slot confirmation */}
            {selectedSlot && (
                <div className="rounded-xl border border-primary/20 bg-primary/10 px-4 py-3">
                    <p className="text-xs font-semibold text-primary uppercase tracking-wide mb-1">
                        Selected time
                    </p>
                    <p className="text-sm font-medium">{selectedSlot}</p>
                </div>
            )}
        </div>
    );
};
