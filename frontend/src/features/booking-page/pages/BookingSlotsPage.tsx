import axios from "axios";
import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { useParams } from "react-router";
import {
    getAvailableSlotsByDate,
    getPublicEvent,
} from "../../bookings/api/BookingsApi";
import { BookingForm } from "../components/BookingForm";
import { EventPanel } from "../components/BookingPanel";
import { BookingSuccess } from "../components/BookingSucces";
import { SlotsPanel } from "../components/SlotsPanel";
import type { PublicEvent, Slot } from "../types/BookingSlots";
import { CalendarPanel } from "../components/CalendarPanel";

const formatDate = (iso: string) =>
    new Date(iso).toLocaleDateString("en-US", {
        weekday: "long",
        month: "long",
        day: "numeric",
    });

const formatTime = (iso: string) =>
    new Date(iso).toLocaleTimeString("en-US", {
        hour: "2-digit",
        minute: "2-digit",
    });

const BookingPage = () => {
    const { shareableId } = useParams() as { shareableId: string };

    const [event, setEvent] = useState<PublicEvent | null>(null);
    const [isLoadingEvent, setLoadingEvent] = useState(true);
    const [slots, setSlots] = useState<Slot[]>([]);
    const [isLoadingSlots, setLoadingSlots] = useState(false);
    const [selectedDate, setSelectedDate] = useState<string | null>(null);
    const [selectedSlot, setSelectedSlot] = useState<Slot | null>(null);
    const [showForm, setShowForm] = useState(false);
    const [booked, setBooked] = useState(false);

    useEffect(() => {
        getPublicEvent(shareableId)
            .then(setEvent)
            .catch((error) => {
                if (axios.isAxiosError(error)) {
                    toast.error(error.response?.data?.message);
                } else {
                    toast.error("Something went wrong");
                }
            })
            .finally(() => setLoadingEvent(false));
    }, [shareableId]);

    const handleSelectDate = async (date: string) => {
        setSelectedDate(date);
        setSelectedSlot(null);
        setShowForm(false);
        setLoadingSlots(true);
        try {
            const data = await getAvailableSlotsByDate(shareableId, date);
            setSlots(data);
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(error.response?.data?.message);
            } else {
                toast.error("Something went wrong");
            }
        } finally {
            setLoadingSlots(false);
        }
    };

    const handleSelectSlot = (slot: Slot) => {
        setSelectedSlot(slot);
        setShowForm(true);
    };

    if (isLoadingEvent)
        return (
            <div className="min-h-screen flex items-center justify-center">
                <span className="loading loading-spinner loading-md text-primary" />
            </div>
        );

    if (!event)
        return (
            <div className="min-h-screen flex items-center justify-center text-base-content/40 text-sm">
                Event not found.
            </div>
        );

    if (booked)
        return (
            <div className="min-h-screen bg-base-200 flex items-center justify-center px-4">
                <div className="bg-base-100 rounded-2xl border border-base-300 p-10 max-w-md w-full">
                    <BookingSuccess eventName={event.eventName} />
                </div>
            </div>
        );

    const selectedSlotLabel = selectedSlot
        ? `${formatDate(selectedSlot.startTime)}, ${formatTime(selectedSlot.startTime)} – ${formatTime(selectedSlot.endTime)}`
        : undefined;

    return (
        <div className="min-h-screen bg-base-200 flex items-center justify-center px-4 py-10">
            <div
                className={`bg-base-100 rounded-2xl border border-base-300 w-full overflow-hidden shadow-sm transition-all duration-300 ${showForm ? "max-w-4xl" : "max-w-6xl"}`}
            >
                {/* ── 3-col: Event + Calendar + Slots ── */}
                {!showForm && (
                    <div className="grid grid-cols-1 md:grid-cols-3 divide-y md:divide-y-0 md:divide-x divide-base-300 items-stretch">
                        {/* Left: Event Details */}
                        <div className="px-6 py-8">
                            <EventPanel
                                event={event}
                                selectedSlot={selectedSlotLabel}
                            />
                        </div>

                        {/* Middle: Calendar */}
                        <div className="px-6 py-8 h-full">
                            <CalendarPanel
                                eventStart={event.eventStart}
                                eventEnd={event.eventEnd}
                                selectedDate={selectedDate}
                                onSelectDate={handleSelectDate}
                            />
                        </div>

                        {/* Right: Slots */}
                        <div className="px-6 py-8 min-h-89 flex flex-col">
                            {!selectedDate ? (
                                <div className="flex-1 flex items-center justify-center">
                                    <p className="text-sm text-base-content/40 text-center">
                                        Select a date to see available time
                                        slots.
                                    </p>
                                </div>
                            ) : (
                                <div className="flex-1 flex flex-col overflow-hidden">
                                    <p className="text-sm font-semibold text-base-content mb-4">
                                        {formatDate(selectedDate + "T00:00:00")}
                                    </p>
                                    <SlotsPanel
                                        slots={slots}
                                        isLoading={isLoadingSlots}
                                        selectedSlotId={
                                            selectedSlot?.id ?? null
                                        }
                                        onSelectSlot={handleSelectSlot}
                                    />
                                </div>
                            )}
                        </div>
                    </div>
                )}

                {/* ── 2-col: Event + Booking Form ── */}
                {showForm && (
                    <div className="grid grid-cols-1 md:grid-cols-2 divide-y md:divide-y-0 md:divide-x divide-base-300">
                        {/* Left: Event Details */}
                        <div className="px-6 py-8">
                            <EventPanel
                                event={event}
                                selectedSlot={selectedSlotLabel}
                            />
                        </div>

                        {/* Right: Booking Form */}
                        <div className="px-6 py-8">
                            <BookingForm
                                event={event}
                                slot={selectedSlot!}
                                onSuccess={() => setBooked(true)}
                                onBack={() => {
                                    setShowForm(false);
                                    setSelectedSlot(null);
                                }}
                            />
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default BookingPage;
