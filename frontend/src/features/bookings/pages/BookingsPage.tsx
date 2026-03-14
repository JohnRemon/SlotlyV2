import { CalendarX, Loader2Icon } from "lucide-react";
import { useState } from "react";
import { useSearchParams } from "react-router";
import { toast } from "sonner";
import { useApiError } from "@/hooks/useApiError";
import { Button } from "@/components/ui/button";
import { BookingCard } from "../components/BookingCard";
import { BookingDetailModal } from "../components/BookingDetailModal";
import { CancelBookingModal } from "../components/CancelBookingModal";
import { useBookings, useCancelBooking, useNoShow } from "../hooks/useBookings";
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
        } catch (error) {
            handleError(error);
        }
    };

    const emptyLabel: Record<BookingTab, string> = {
        CONFIRMED: "upcoming",
        CANCELLED: "cancelled",
        NO_SHOW: "no-show",
        PAST: "past",
    };

    return (
        <div className="mx-auto flex w-full max-w-7xl flex-col gap-6">
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
                <div className="rounded-2xl border border-dashed bg-card/40 p-10 shadow-sm ring-1 ring-foreground/5 supports-backdrop-filter:backdrop-blur-sm">
                    <div className="mx-auto flex max-w-sm flex-col items-center justify-center gap-4 text-center">
                        <div className="flex size-14 items-center justify-center rounded-2xl bg-muted/40 ring-1 ring-foreground/10">
                            <CalendarX className="size-6 text-muted-foreground" />
                        </div>
                        <div>
                            <p className="text-sm font-semibold tracking-[-0.01em]">
                                No {emptyLabel[activeTab]} bookings yet
                            </p>
                            <p className="mt-1 text-xs text-muted-foreground">
                                Bookings will appear here once they're made
                            </p>
                        </div>
                    </div>
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
