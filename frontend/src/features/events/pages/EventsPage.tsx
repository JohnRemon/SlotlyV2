import { useDisclosure } from "@mantine/hooks";
import { CalendarX, Plus, Search } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import CreateEventModal from "../components/CreateEventModal";
import { DeleteEventModal } from "../components/DeleteEventModal";
import { EventCard } from "../components/EventCard";
import { EventCardSkeleton } from "../components/EventCardSkeleton";
import {
    useCreateEvent,
    useDeleteEvent,
    useEvents,
    useUpdateEventVisibility,
} from "../hooks/useEvents";
import type { EventRequest, EventResponse } from "../types/Event";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

const EventsPage = () => {
    const { data: events = [], isLoading } = useEvents();
    const createEventMutation = useCreateEvent();
    const updateVisibilityMutation = useUpdateEventVisibility();
    const deleteEventMutation = useDeleteEvent();
    const [query, setQuery] = useState("");
    const [createOpen, { open: openCreate, close: closeCreate }] =
        useDisclosure(false);
    const [eventToDelete, setEventToDelete] = useState<EventResponse | null>(
        null,
    );

    const normalizedQuery = query.toLowerCase();
    const filtered = events
        .filter((e) => e.id != null)
        .filter((e) =>
            (e.eventName ?? "Untitled event")
                .toLowerCase()
                .includes(normalizedQuery),
        );

    const handleDelete = async (event: EventResponse) => {
        await deleteEventMutation.mutateAsync(event.id);
        setEventToDelete(null);
        toast.success("Event deleted");
    };

    const handleCreate = async (payload: EventRequest) => {
        await createEventMutation.mutateAsync(payload);
        closeCreate();
        toast.success("Event created");
    };

    const handleToggleVisibility = async (id: number, isPublic: boolean) => {
        await updateVisibilityMutation.mutateAsync({ id, isPublic });
    };

    return (
        <div className="mx-auto flex w-full max-w-7xl flex-col gap-6">
            <div className="flex items-center gap-2">
                <div className="relative flex-1">
                    <Search className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                    <Input
                        type="text"
                        className="pl-9"
                        placeholder="Search events..."
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                    />
                </div>
                <Button type="button" onClick={openCreate}>
                    <Plus className="size-4" />
                    New event
                </Button>
            </div>

            {isLoading ? (
                <div className="flex flex-col gap-2">
                    {[
                        "events-skeleton-1",
                        "events-skeleton-2",
                        "events-skeleton-3",
                    ].map((key) => (
                        <EventCardSkeleton key={key} />
                    ))}
                </div>
            ) : filtered.length === 0 ? (
                <EmptyEvents query={query} onCreateClick={openCreate} />
            ) : (
                <div className="flex flex-col gap-2">
                    {filtered.map((event) => (
                        <EventCard
                            key={event.id}
                            event={event}
                            onDelete={setEventToDelete}
                            onToggleVisibility={handleToggleVisibility}
                        />
                    ))}
                </div>
            )}

            <CreateEventModal
                open={createOpen}
                onClose={closeCreate}
                onCreate={handleCreate}
            />
            <DeleteEventModal
                event={eventToDelete}
                onConfirm={handleDelete}
                onClose={() => setEventToDelete(null)}
            />
        </div>
    );
};

interface EmptyEventsProps {
    query: string;
    onCreateClick: () => void;
}

const EmptyEvents = ({ query, onCreateClick }: EmptyEventsProps) => (
    <div className="rounded-2xl border border-dashed bg-card/40 p-10 shadow-sm ring-1 ring-foreground/5 supports-backdrop-filter:backdrop-blur-sm">
        <div className="mx-auto flex max-w-sm flex-col items-center justify-center gap-4 text-center">
            <div className="flex size-14 items-center justify-center rounded-2xl bg-muted/40 ring-1 ring-foreground/10">
                <CalendarX className="size-6 text-muted-foreground" />
            </div>
            <div>
                <p className="text-sm font-semibold tracking-[-0.01em]">
                    {query ? `No results for "${query}"` : "No events yet"}
                </p>
                <p className="mt-1 text-xs text-muted-foreground">
                    {query
                        ? "Try a different name or create a new event"
                        : "Create your first event to get started"}
                </p>
            </div>
            <Button type="button" onClick={onCreateClick}>
                <Plus className="size-4" />
                {query ? "Create event" : "New event"}
            </Button>
        </div>
    </div>
);

export default EventsPage;
