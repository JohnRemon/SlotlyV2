import { CalendarX, Loader2Icon } from "lucide-react";
import { useState } from "react";
import { useSearchParams } from "react-router";
import { toast } from "sonner";
import { useApiError } from "@/hooks/useApiError";
import { Button } from "@/components/ui/button";
import { BookingCard } from "../components/BookingCard";
import { BookingDetailModal } from "../components/BookingDetailModal";
import { CancelBookingModal } from "../components/CancelBookingModal";
import {
    useBookings,
    useCancelBooking,
    useNoShow,
} from "../hooks/useBookings";
import type { BookingResponse, BookingTab } from "../types/Booking";
import { isPast } from "../utils/DateUtils";

const TABS: { label: string; value: BookingTab }[] = [
    { label: "Upcoming", value: "CONFIRMED" },
    { label: "Cancelled", value: "CANCELLED" },
    { label: "No Show", value: "NO_SHOW" },
    { label: "Past", value: "PAST" },
];

const BookingsPage = () => {
    const { data: bookings = [], isLoading } = useBookings();
    const cancelBookingMutation = useCancelBooking();
    const noShowMutation = useNoShow();
    const handleError = useApiError();

    const [searchParams, setSearchParams] = useSearchParams();
    const [viewBooking, setViewBooking] = useState<BookingResponse | null>(
        null,
    );
    const [cancelBooking, setCancelBooking] = useState<BookingResponse | null>(
        null,
    );

    const activeTab = (searchParams.get("tab") as BookingTab) ?? "CONFIRMED";

    const handleTabChange = (tab: BookingTab) => setSearchParams({ tab });

    const filtered = bookings.filter((booking) => {
        if (activeTab === "CONFIRMED") {
            return (
                booking.bookingStatus === "CONFIRMED" &&
                !isPast(booking.endTime)
            );
        }
        if (activeTab === "PAST") {
            return (
                booking.bookingStatus === "CONFIRMED" && isPast(booking.endTime)
            );
        }
        return booking.bookingStatus === activeTab;
    });

    const handleCancel = async (
        id: number,
        attendeeEmail: string,
        reason: string,
    ) => {
        try {
            await cancelBookingMutation.mutateAsync({
                id,
                request: {
                    bookingId: id,
                    attendeeEmail,
                    cancellationReason: reason,
                },
            });
            toast.success("Booking cancelled.");
        } catch (e) {
            handleError(e);
        }
    };

    const handleNoShow = async (booking: BookingResponse) => {
        try {
            await noShowMutation.mutateAsync(booking.id);
            toast.success("Marked as no-show.");
        } catch (e) {
            handleError(e);
        }
    };

    return (
        <div className="mx-auto flex w-full max-w-5xl flex-col gap-6 px-4 py-8">
            <div className="flex items-start justify-between gap-4">
                <div className="min-w-0">
                    <h1 className="text-base font-semibold tracking-[-0.01em]">
                        Bookings
                    </h1>
                    <p className="mt-1 text-xs text-muted-foreground">
                        Review attendee details, reschedule, cancel, and mark
                        no-shows.
                    </p>
                </div>
            </div>

            <div className="inline-flex w-fit items-center gap-1 rounded-xl bg-muted/40 p-1 ring-1 ring-border">
                {TABS.map(({ label, value }) => (
                    <Button
                        key={value}
                        type="button"
                        onClick={() => handleTabChange(value)}
                        size="sm"
                        variant={activeTab === value ? "secondary" : "ghost"}
                        className="h-8 rounded-lg px-3"
                    >
                        {label}
                    </Button>
                ))}
            </div>

            {isLoading ? (
                <div className="flex justify-center py-16">
                    <Loader2Icon className="size-5 animate-spin text-muted-foreground" />
                </div>
            ) : filtered.length === 0 ? (
                <div className="flex flex-col items-center justify-center gap-4 rounded-xl border border-dashed border-border bg-card/20 p-10 text-center">
                    <div className="flex size-14 items-center justify-center rounded-full bg-muted ring-1 ring-border">
                        <CalendarX className="size-6 text-muted-foreground" />
                    </div>
                    <p className="text-sm font-medium text-muted-foreground">
                        No {activeTab.toLowerCase()} bookings yet
                    </p>
                </div>
            ) : (
                <div className="flex flex-col gap-2">
                    {filtered.map((booking) => (
                        <BookingCard
                            key={booking.id}
                            booking={booking}
                            onView={setViewBooking}
                        />
                    ))}
                </div>
            )}

            <BookingDetailModal
                booking={viewBooking}
                onClose={() => setViewBooking(null)}
                onCancel={(b) => {
                    setViewBooking(null);
                    setCancelBooking(b);
                }}
                onNoShow={handleNoShow}
            />
            <CancelBookingModal
                booking={cancelBooking}
                onConfirm={handleCancel}
                onClose={() => setCancelBooking(null)}
            />
        </div>
    );
};

export default BookingsPage;
