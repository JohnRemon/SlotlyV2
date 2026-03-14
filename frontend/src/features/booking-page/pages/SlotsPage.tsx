import { EventsApi } from "@/features/events/api/EventsApi";
import type { PublicEventResponse } from "@/features/events/types/Event";
import { SlotsApi } from "@/features/slots/api/SlotsApi";
import type { SlotResponse } from "@/features/slots/types/Slots";
import { useApiError } from "@/hooks/useApiError";
import { Loader2Icon } from "lucide-react";
import { useCallback, useEffect, useState } from "react";
import { useParams } from "react-router";
import { BookingForm } from "../components/BookingForm";
import { BookingSuccess } from "../components/BookingSucces";
import { CalendarPanel } from "../components/CalendarPanel";
import { SlotsPanel } from "../components/SlotsPanel";
import { EventPanel } from "../components/EventPanel";

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

const SlotsPage = () => {
    const { shareableId } = useParams() as { shareableId: string };
    const handleError = useApiError();

    const [event, setEvent] = useState<PublicEventResponse | null>(null);
    const [isLoadingEvent, setLoadingEvent] = useState(true);
    const [slots, setSlots] = useState<SlotResponse[]>([]);
    const [isLoadingSlots, setLoadingSlots] = useState(false);
    const [selectedDate, setSelectedDate] = useState<string | null>(null);
    const [selectedSlot, setSelectedSlot] = useState<SlotResponse | null>(null);
    const [showForm, setShowForm] = useState(false);
    const [booked, setBooked] = useState(false);

    useEffect(() => {
        EventsApi.getByShareableId(shareableId)
            .then((res) => setEvent(res.data.data))
            .catch((error) => handleError(error))
            .finally(() => setLoadingEvent(false));
    }, [shareableId, handleError]);

    const handleSelectDate = useCallback(
        async (date: string) => {
            setSelectedDate(date);
            setSelectedSlot(null);
            setShowForm(false);
            setLoadingSlots(true);
            try {
                const res = await SlotsApi.getAvailable(shareableId, date);
                setSlots(res.data.content);
            } catch (error) {
                handleError(error);
            } finally {
                setLoadingSlots(false);
            }
        },
        [shareableId, handleError],
    );

    useEffect(() => {
        if (!event) return;
        const firstDay = new Date();
        const month = String(firstDay.getMonth() + 1).padStart(2, "0");
        const day = String(firstDay.getDate()).padStart(2, "0");
        handleSelectDate(`${firstDay.getFullYear()}-${month}-${day}`);
    }, [event, handleSelectDate]);

    const handleSelectSlot = (slot: SlotResponse) => {
        setSelectedSlot(slot);
        setShowForm(true);
    };

    const selectedSlotLabel = selectedSlot
        ? `${formatDate(selectedSlot.startTime)}, ${formatTime(selectedSlot.startTime)} – ${formatTime(selectedSlot.endTime)}`
        : undefined;

    if (isLoadingEvent) {
        return (
            <div className="flex min-h-dvh items-center justify-center bg-background">
                <Loader2Icon className="size-5 animate-spin text-muted-foreground" />
            </div>
        );
    }

    if (!event) {
        return (
            <div className="flex min-h-dvh items-center justify-center bg-background">
                <div className="flex flex-col items-center gap-2 text-center">
                    <p className="text-sm font-semibold tracking-[-0.01em]">
                        Event not found
                    </p>
                    <p className="text-xs text-muted-foreground">
                        This link may be invalid or the event has been removed.
                    </p>
                </div>
            </div>
        );
    }

    if (booked) {
        return (
            <div className="flex min-h-dvh items-center justify-center bg-linear-to-b from-background to-muted/30 px-4 py-10">
                <div className="w-full max-w-md rounded-2xl bg-card p-10 shadow-sm ring-1 ring-foreground/10">
                    <BookingSuccess eventName={event.eventName} />
                </div>
            </div>
        );
    }

    return (
        <div className="flex min-h-dvh items-center justify-center bg-linear-to-b from-background to-muted/30 px-4 py-10">
            <div
                className={[
                    "w-full overflow-hidden rounded-2xl bg-card shadow-sm ring-1 ring-foreground/10 transition-all duration-300",
                    showForm ? "max-w-4xl" : "max-w-6xl",
                ].join(" ")}
            >
                {!showForm ? (
                    <div className="grid grid-cols-1 items-stretch divide-y divide-border md:grid-cols-3 md:divide-x md:divide-y-0">
                        <div className="px-7 py-8">
                            <EventPanel
                                event={event}
                                selectedSlot={selectedSlotLabel}
                            />
                        </div>

                        <div className="px-7 py-8">
                            <CalendarPanel
                                selectedDate={selectedDate}
                                onSelectDate={handleSelectDate}
                                schedule={event.schedule}
                            />
                        </div>

                        <div className="flex min-h-96 flex-col px-6 py-8">
                            {!selectedDate ? (
                                <div className="flex flex-1 items-center justify-center">
                                    <p className="text-center text-xs text-muted-foreground">
                                        Select a date to see available time
                                        slots.
                                    </p>
                                </div>
                            ) : (
                                <div className="flex flex-1 flex-col overflow-hidden">
                                    <p className="mb-4 text-sm font-semibold tracking-[-0.01em]">
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
                ) : (
                    <div className="grid grid-cols-1 divide-y divide-border md:grid-cols-2 md:divide-x md:divide-y-0">
                        <div className="px-7 py-8">
                            <EventPanel
                                event={event}
                                selectedSlot={selectedSlotLabel}
                            />
                        </div>

                        <div className="px-7 py-8">
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

export default SlotsPage;
