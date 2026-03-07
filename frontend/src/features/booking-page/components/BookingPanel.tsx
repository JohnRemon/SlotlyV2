import { Clock, Globe, FileText } from "lucide-react";
import type { PublicEvent } from "../types/BookingSlots";

interface EventPanelProps {
    event: PublicEvent;
    selectedSlot?: string;
}

export const EventPanel = ({ event, selectedSlot }: EventPanelProps) => {
    const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
    const firstName = event.host?.firstName ?? "Host";
    const lastName = event.host?.lastName ?? "";
    const initial = firstName.charAt(0).toUpperCase();

    return (
        <div className="flex flex-col gap-5">
            {/* Host */}
            <div className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-full bg-primary text-white flex items-center justify-center font-bold text-sm shrink-0">
                    {initial}
                </div>
                <span className="text-sm text-base-content/65">
                    {firstName} {lastName}
                </span>
            </div>

            {/* Event Name */}
            <div>
                <h1 className="text-2xl font-bold text-base-content leading-tight">
                    {event.eventName}
                </h1>
            </div>

            {/* Meta */}
            <div className="flex flex-col gap-2.5">
                <div className="flex items-center gap-2 text-sm text-base-content/60">
                    <Clock className="w-4 h-4 shrink-0" />
                    {event.availabilityRulesDTO.slotDurationMinutes} min
                </div>
                <div className="flex items-center gap-2 text-sm text-base-content/60">
                    <Globe className="w-4 h-4 shrink-0" />
                    {timezone}
                </div>
                {event.description && (
                    <div className="flex items-start gap-2 text-sm text-base-content/60">
                        <FileText className="w-4 h-4 shrink-0 mt-0.5" />
                        <p>{event.description}</p>
                    </div>
                )}
            </div>

            {/* Selected slot confirmation */}
            {selectedSlot && (
                <div className="bg-primary/10 border border-primary/20 rounded-xl px-4 py-3">
                    <p className="text-xs font-semibold text-primary uppercase tracking-wide mb-1">
                        Selected time
                    </p>
                    <p className="text-sm font-medium text-base-content">
                        {selectedSlot}
                    </p>
                </div>
            )}
        </div>
    );
};
