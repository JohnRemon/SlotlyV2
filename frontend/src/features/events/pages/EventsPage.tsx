import { CalendarX, Plus, Search } from "lucide-react";
import { useState } from "react";
import CreateEventModal from "../components/CreateEventModal";
import { EventRow } from "../components/EventRow";
import { useEvents } from "../hooks/useEvents";
import { DeleteEventModal } from "../components/DeleteEventModal";
import type { Event } from "../types/Event";
import toast from "react-hot-toast";
import axios from "axios";
import { updateEvent } from "../api/EventsApi";

const EventsPage = () => {
    const { events, isLoading, create, remove } = useEvents();
    const [query, setQuery] = useState("");
    const [showModal, setShowModal] = useState(false);
    const [eventToDelete, setEventToDelete] = useState<Event | null>(null);

    const filtered = events.filter((e) =>
        e.eventName.toLowerCase().includes(query.toLowerCase()),
    );

    const handleDelete = async (event: Event) => {
        try {
            await remove(event.id);
            toast.success("Event Deleted!");
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(error.response?.data?.message);
            } else {
                toast.error("Something went wrong");
            }
        }
    };

    const handleTogglePublic = async (event: Event, isPublic: boolean) => {
        await updateEvent(
            {
                ...event,
                availabilityRulesDTO: {
                    ...event.availabilityRulesDTO,
                    isPublic,
                },
            },
            event.id,
        );
    };

    return (
        <div className="max-w-7xl mx-auto py-8 px-4 flex flex-col gap-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-bold text-base-content">
                        Events
                    </h1>
                    <p className="text-sm text-base-content/50 mt-1">
                        Manage your bookable events
                    </p>
                </div>
                <button
                    className="btn btn-primary btn-sm gap-2 rounded-sm"
                    onClick={() => setShowModal(true)}
                >
                    <Plus className="w-4 h-4" />
                    New event
                </button>
            </div>

            {/* Search */}
            <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-base-content/30" />
                <input
                    type="text"
                    className="input input-bordered w-full pl-9 rounded-lg outline-none"
                    placeholder="Search"
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                />
            </div>

            {/* List */}
            {isLoading ? (
                <div className="flex justify-center py-16">
                    <span className="loading loading-spinner loading-md text-primary" />
                </div>
            ) : filtered.length === 0 ? (
                <div className="flex flex-col items-center justify-center py-16 gap-4 border-dashed border-base-content/30 border rounded-sm">
                    <div className="w-14 h-14 rounded-full bg-base-300 flex items-center justify-center">
                        <CalendarX className="w-6 h-6 text-base-content/30" />
                    </div>
                    {query ? (
                        <>
                            <div className="text-center">
                                <p className="font-semibold text-base-content/60 text-sm">
                                    No results for "{query}"
                                </p>
                                <p className="text-xs text-base-content/40 mt-1">
                                    Try a different name or create a new event
                                </p>
                            </div>
                            <button
                                className="btn btn-primary btn-sm gap-2"
                                onClick={() => setShowModal(true)}
                            >
                                <Plus className="w-4 h-4" />
                                Create Event
                            </button>
                        </>
                    ) : (
                        <>
                            <div className="text-center">
                                <p className="font-semibold text-base-content/60 text-sm">
                                    No events yet
                                </p>
                                <p className="text-xs text-base-content/40 mt-1">
                                    Create your first event to get started
                                </p>
                            </div>
                            <button
                                className="btn btn-primary btn-sm gap-2"
                                onClick={() => setShowModal(true)}
                            >
                                <Plus className="w-4 h-4" />
                                New event
                            </button>
                        </>
                    )}
                </div>
            ) : (
                <div className="flex flex-col gap-2">
                    {filtered.map((event) => (
                        <EventRow
                            key={event.id}
                            event={event}
                            onDelete={setEventToDelete}
                            onTogglePublic={handleTogglePublic}
                        />
                    ))}
                </div>
            )}

            {/* Modal */}
            {showModal && (
                <CreateEventModal
                    onClose={() => setShowModal(false)}
                    onCreate={create}
                />
            )}

            <DeleteEventModal
                event={eventToDelete}
                onConfirm={handleDelete}
                onClose={() => setEventToDelete(null)}
            />
        </div>
    );
};

export default EventsPage;
