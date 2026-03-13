import { Calendar, Clock, Mail, User } from "lucide-react";
import type { ComponentProps } from "react";
import type { BookingResponse } from "../types/Booking";
import { formatDate, formatTime, isPast } from "../utils/DateUtils";
import { Badge } from "@/components/ui/badge";

type DisplayStatus = BookingResponse["bookingStatus"] | "PAST";

const getStatus = (booking: BookingResponse): DisplayStatus => {
    if (booking.bookingStatus === "CONFIRMED" && isPast(booking.endTime))
        return "PAST";
    return booking.bookingStatus;
};

const statusStyles: Record<
    DisplayStatus,
    NonNullable<ComponentProps<typeof Badge>["variant"]>
> = {
    CONFIRMED: "default",
    CANCELLED: "destructive",
    NO_SHOW: "secondary",
    PAST: "outline",
};

const statusLabels: Record<DisplayStatus, string> = {
    CONFIRMED: "Confirmed",
    CANCELLED: "Cancelled",
    NO_SHOW: "No Show",
    PAST: "Past",
};

interface BookingCardProps {
    booking: BookingResponse;
    onView: (booking: BookingResponse) => void;
}

export const BookingCard = ({ booking, onView }: BookingCardProps) => {
    const status = getStatus(booking);

    return (
        <button
            type="button"
            className="flex w-full items-center gap-4 rounded-xl bg-card px-5 py-4 text-left ring-1 ring-foreground/10 transition-colors hover:bg-muted/20 focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-ring/50"
            onClick={() => onView(booking)}
        >
            <div className="flex size-10 shrink-0 items-center justify-center rounded-full bg-primary/15 text-sm font-semibold text-primary ring-1 ring-primary/15">
                {booking.attendeeName[0].toUpperCase()}
            </div>
            <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 mb-1">
                    <span className="font-semibold text-sm truncate">
                        {booking.attendeeName}
                    </span>
                    <Badge variant={statusStyles[status]}>
                        {statusLabels[status]}
                    </Badge>
                </div>
                <div className="flex flex-wrap items-center gap-3 text-xs text-muted-foreground">
                    <span className="flex items-center gap-1">
                        <Mail className="size-3" />
                        {booking.attendeeEmail}
                    </span>
                    <span className="flex items-center gap-1">
                        <User className="size-3" />
                        {booking.eventName}
                    </span>
                </div>
            </div>
            <div className="text-right shrink-0 hidden sm:block">
                <div className="mb-0.5 flex items-center justify-end gap-1 text-xs text-foreground/70">
                    <Calendar className="size-3" />
                    {formatDate(booking.startTime)}
                </div>
                <div className="flex items-center justify-end gap-1 text-xs text-muted-foreground">
                    <Clock className="size-3" />
                    {formatTime(booking.startTime)} –{" "}
                    {formatTime(booking.endTime)}
                </div>
            </div>
        </button>
    );
};
