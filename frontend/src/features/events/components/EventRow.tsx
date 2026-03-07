import { Clock, Copy, ExternalLink, Trash } from "lucide-react";
import type React from "react";
import toast from "react-hot-toast";
import type { Event } from "../types/Event";
import { useState } from "react";
import { updateEvent } from "../api/EventsApi";

interface EventRowProps {
    event: Event;
    onDelete: (event: Event) => void;
    onTogglePublic: (event: Event, isPublic: boolean) => Promise<void>;
}

export const EventRow = ({ event, onDelete }: EventRowProps) => {
    const bookingUrl = `${window.location.origin}/book/${event.shareableId}`;

    const [isPublic, setIsPublic] = useState(
        event.availabilityRulesDTO.isPublic,
    );

    const handleCopy = async (e: React.MouseEvent) => {
        e.stopPropagation();
        navigator.clipboard.writeText(bookingUrl);
        toast.success("Link copied!");
    };

    const handleTogglePublic = async (
        e: React.ChangeEvent<HTMLInputElement>,
    ) => {
        const value = e.target.checked;
        setIsPublic(value);
        try {
            await updateEvent(
                {
                    eventName: event.eventName,
                    description: event.description,
                    eventStart: event.eventStart,
                    eventEnd: event.eventEnd,
                    availabilityRulesDTO: {
                        ...event.availabilityRulesDTO,
                        isPublic,
                    },
                },
                event.id,
            );
        } catch {
            setIsPublic(!value);
            toast.error("Failed to update event.");
        }
    };

    return (
        <div className="bg-base-100 border border-base-300 rounded-xl px-5 py-4 flex items-center gap-4 hover:border-base-content/20 transition-colors group">
            {/* name */}
            <div className="flex items-center gap-3 flex-1 min-w-0">
                <div className="min-w-0">
                    <p className="font-bold text-sm text-base-content truncate">
                        {event.eventName}
                    </p>
                    {/* Duration */}
                    <div className="flex font-semibold items-center gap-1 text-xs text-base-content/75 bg-base-content/30 px-1 py-0.5 rounded-sm w-fit mt-1">
                        <Clock className="w-3.5 h-3.5" />
                        {event.availabilityRulesDTO.slotDurationMinutes} min
                    </div>
                </div>
            </div>

            {/* Actions */}
            <div className="flex items-center gap-3">
                <span className="text-xs font-medium text-base-content/60 bg-base-content/15 px-2 py-1 rounded-sm">
                    {isPublic ? "Public" : "Private"}
                </span>
                <input
                    type="checkbox"
                    className="toggle toggle-primary toggle-sm"
                    checked={isPublic}
                    onChange={handleTogglePublic}
                />
            </div>
            <div className="flex items-center gap-1 shrink-0">
                <button
                    className="btn btn-ghost btn-xs gap-1 hidden sm:flex hover:rounded-lg"
                    onClick={handleCopy}
                    title="Copy booking link"
                >
                    <Copy className="w-3.5 h-3.5" />
                </button>
                <a
                    href={`/book/${event.shareableId}`}
                    className="btn btn-ghost btn-xs gap-1 hidden sm:flex hover: rounded-lg"
                    title="View booking page"
                    target="_blank"
                    onClick={(e) => e.stopPropagation()}
                >
                    <ExternalLink className="w-3.5 h-3.5" />
                </a>
                <button
                    className="btn btn-ghost btn-xs gap-1 hidden sm:flex text-error hover:bg-error/10 rounded-lg"
                    onClick={(e) => {
                        e.stopPropagation();
                        onDelete(event);
                    }}
                    title="Delete Event"
                >
                    <Trash className="w-3.5 h-3.5" />
                </button>
            </div>
        </div>
    );
};
