import axios from "axios";
import { useState } from "react";
import toast from "react-hot-toast";
import { useBookings } from "../hooks/useBookings";
import type { BookingTab, Booking } from "../types/Booking";
import { BookingCard } from "../components/BookingCard";
import { BookingDetailModal } from "../components/BookingDetailModal";
import { CancelBookingModal } from "../components/CancelBookingModal";
import { isPast } from "../utils/DateUtils";

// ─── Tab Config ───────────────────────────────────────────────────────────────

const TABS: { label: string; value: BookingTab }[] = [
    { label: "Upcoming", value: "CONFIRMED" },
    { label: "Cancelled", value: "CANCELLED" },
    { label: "No Show", value: "NO_SHOW" },
    { label: "Past", value: "PAST" },
];

// ─── Component ───────────────────────────────────────────────────────────────

const BookingsPage = () => {
    const { bookings, isLoading, cancel, noShow } = useBookings();

    const [activeTab, setActiveTab] = useState<BookingTab>("CONFIRMED");
    const [viewBooking, setViewBooking] = useState<Booking | null>(null);
    const [cancelBooking, setCancelBooking] = useState<Booking | null>(null);

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
            await cancel(id, attendeeEmail, reason);
            toast.success("Booking cancelled.");
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(error.response?.data?.message);
            } else {
                toast.error("Something went wrong.");
            }
        }
    };

    const handleNoShow = async (booking: Booking) => {
        try {
            await noShow(booking.id);
            toast.success("Marked as no-show.");
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(error.response?.data?.message);
            } else {
                toast.error("Something went wrong.");
            }
        }
    };

    return (
        <div className="max-w-7xl mx-auto py-8 px-4 flex flex-col gap-6">
            {/* Tabs */}
            <div className="flex items-center gap-1 bg-base-300 p-1 rounded-2xl w-fit">
                {TABS.map(({ label, value }) => (
                    <button
                        key={value}
                        onClick={() => setActiveTab(value)}
                        className={`px-3 py-1.5 rounded-xl text-sm font-medium transition-all cursor-pointer ${
                            activeTab === value
                                ? "bg-base-100 text-base-content shadow-sm"
                                : "text-base-content/50 hover:text-base-content hover:bg-base-200"
                        }`}
                    >
                        {label}
                    </button>
                ))}
            </div>

            {/* List */}
            {isLoading ? (
                <div className="flex justify-center py-16">
                    <span className="loading loading-spinner loading-md text-primary" />
                </div>
            ) : filtered.length === 0 ? (
                <div className="flex flex-col items-center justify-center py-16 text-base-content/30 gap-2">
                    <p className="text-sm">No bookings here</p>
                </div>
            ) : (
                <div className="flex flex-col gap-2">
                    {filtered.map((booking) => (
                        <BookingCard
                            key={booking.id}
                            booking={booking}
                            onView={setViewBooking}
                            onCancel={setCancelBooking}
                            onNoShow={handleNoShow}
                        />
                    ))}
                </div>
            )}

            {/* Modals */}
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
