import { Calendar, Clock, Mail, User } from "lucide-react";
import type { Booking } from "../types/Booking.ts";
import { formatDate, formatTime, isPast } from "../utils/DateUtils.ts";

// ───  Helpers ─────────────────────────────────────────────────────────────

const getStatus = (booking: Booking) => {
    if (booking.bookingStatus === "CONFIRMED" && isPast(booking.endTime))
        return "PAST";
    return booking.bookingStatus;
};

// ─── Status Badge ─────────────────────────────────────────────────────────────

const statusStyles: Record<Booking["bookingStatus"], string> = {
    CONFIRMED: "badge-success",
    CANCELLED: "badge-error",
    NO_SHOW: "badge-warning",
    PAST: "badge-ghost",
};

const statusLabels: Record<Booking["bookingStatus"], string> = {
    CONFIRMED: "Confirmed",
    CANCELLED: "Cancelled",
    NO_SHOW: "No Show",
    PAST: "Past",
};

// ─── Props ───────────────────────────────────────────────────────────────────

interface BookingCardProps {
    booking: Booking;
    onView: (booking: Booking) => void;
    onCancel: (booking: Booking) => void;
    onNoShow: (booking: Booking) => void;
}

// ─── Component ───────────────────────────────────────────────────────────────

export const BookingCard = ({ booking, onView }: BookingCardProps) => {
    return (
        <div
            className="bg-base-100 border border-base-300 rounded-xl px-5 py-4 flex items-center gap-4 hover:border-base-content/20 transition-colors cursor-pointer group"
            onClick={() => onView(booking)}
        >
            {/* Avatar */}
            <div className="w-10 h-10 rounded-full bg-primary/10 text-primary flex items-center justify-center font-semibold text-sm shrink-0">
                {booking.attendeeName[0].toUpperCase()}
            </div>

            {/* Main Info */}
            <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 mb-1">
                    <span className="font-semibold text-sm text-base-content truncate">
                        {booking.attendeeName}
                    </span>
                    <span
                        className={`badge badge-sm rounded-xl ${statusStyles[getStatus(booking)]}`}
                    >
                        {statusLabels[getStatus(booking)]}
                    </span>
                </div>
                <div className="flex items-center gap-3 text-xs text-base-content/50 flex-wrap">
                    <span className="flex items-center gap-1">
                        <Mail className="w-3 h-3" />
                        {booking.attendeeEmail}
                    </span>
                    <span className="flex items-center gap-1">
                        <User className="w-3 h-3" />
                        {booking.eventName}
                    </span>
                </div>
            </div>

            {/* Date & Time */}
            <div className="text-right shrink-0 hidden sm:block">
                <div className="flex items-center gap-1 text-xs text-base-content/70 justify-end mb-0.5">
                    <Calendar className="w-3 h-3" />
                    {formatDate(booking.startTime)}
                </div>
                <div className="flex items-center gap-1 text-xs text-base-content/50 justify-end">
                    <Clock className="w-3 h-3" />
                    {formatTime(booking.startTime)} –{" "}
                    {formatTime(booking.endTime)}
                </div>
            </div>
        </div>
    );
};
