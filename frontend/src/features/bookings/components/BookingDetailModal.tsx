import {
    Ban,
    Calendar,
    Clock,
    FileText,
    Mail,
    RotateCcw,
    XIcon,
} from "lucide-react";
import { useNavigate } from "react-router";
import type { BookingResponse } from "../types/Booking";
import { isPast, formatDate, formatTime } from "../utils/DateUtils";

import { Button } from "@/components/ui/button";
import {
    Dialog,
    DialogClose,
    DialogContent,
    DialogTitle,
} from "@/components/ui/dialog";

// --- Props -------------------------------------------------------------------

interface BookingDetailModalProps {
    booking: BookingResponse | null;
    onClose: () => void;
    onCancel: (booking: BookingResponse) => void;
    onNoShow: (booking: BookingResponse) => void;
}

// --- Component ---------------------------------------------------------------

export const BookingDetailModal = ({
    booking,
    onClose,
    onCancel,
    onNoShow,
}: BookingDetailModalProps) => {
    const navigate = useNavigate();

    if (!booking) return null;

    const isUpcoming =
        booking.bookingStatus === "CONFIRMED" && !isPast(booking.endTime);
    const isCancelled = booking.bookingStatus === "CANCELLED";
    const hasFormAnswers =
        booking.formAnswers !== undefined && booking.formAnswers.length > 0;

    return (
        <Dialog open onOpenChange={(open) => !open && onClose()}>
            <DialogContent
                showCloseButton={false}
                // TODO: change hardcoded values
                className="max-h-[calc(100dvh-2rem)] gap-0 overflow-hidden p-0 sm:max-w-lg"
            >
                <div className="flex items-center justify-between border-b px-6 py-4">
                    <DialogTitle>Booking details</DialogTitle>
                    <DialogClose asChild>
                        <Button
                            type="button"
                            variant="ghost"
                            size="icon-sm"
                            className="rounded-full"
                            aria-label="Close"
                        >
                            <XIcon className="size-4" />
                            <span className="sr-only">Close</span>
                        </Button>
                    </DialogClose>
                </div>

                <div className="flex-1 overflow-y-auto px-6 py-5">
                    <div className="flex flex-col gap-5">
                        {/* Attendee */}
                        <div className="flex items-center gap-3">
                            <div className="flex size-11 shrink-0 items-center justify-center rounded-full bg-primary/15 text-primary ring-1 ring-primary/15">
                                <span className="text-sm font-bold">
                                    {booking.attendeeName[0].toUpperCase()}
                                </span>
                            </div>
                            <div className="min-w-0">
                                <p className="truncate text-sm font-semibold">
                                    {booking.attendeeName}
                                </p>
                                <p className="mt-0.5 flex items-center gap-1 text-xs text-muted-foreground">
                                    <Mail className="size-3" />
                                    <span className="truncate">
                                        {booking.attendeeEmail}
                                    </span>
                                </p>
                            </div>
                        </div>

                        <div className="h-px bg-border" />

                        {/* Booking details */}
                        <div className="flex flex-col gap-3 text-sm">
                            <Row
                                icon={<Calendar className="size-4" />}
                                label="Date"
                                value={formatDate(booking.startTime)}
                            />
                            <Row
                                icon={<Clock className="size-4" />}
                                label="Time"
                                value={`${formatTime(booking.startTime)} – ${formatTime(booking.endTime)}`}
                            />
                            <Row
                                icon={<FileText className="size-4" />}
                                label="Event"
                                value={booking.eventName}
                            />
                            {booking.notes && (
                                <Row
                                    icon={<FileText className="size-4" />}
                                    label="Notes"
                                    value={booking.notes}
                                />
                            )}
                        </div>

                        {/* Cancellation info */}
                        {isCancelled && (
                            <>
                                <div className="h-px bg-border" />
                                <div className="rounded-xl border border-destructive/30 bg-destructive/10 p-4">
                                    <p className="text-xs font-semibold uppercase tracking-wide text-destructive">
                                        Cancellation
                                    </p>

                                    <div className="mt-3 flex flex-col gap-3">
                                        {booking.cancelledAt && (
                                            <Row
                                                icon={
                                                    <Clock className="size-4" />
                                                }
                                                label="Cancelled at"
                                                value={formatDate(
                                                    booking.cancelledAt,
                                                )}
                                            />
                                        )}
                                        {booking.cancellationReason ? (
                                            <Row
                                                icon={
                                                    <Ban className="size-4" />
                                                }
                                                label="Reason"
                                                value={
                                                    booking.cancellationReason
                                                }
                                            />
                                        ) : (
                                            <p className="text-xs text-muted-foreground">
                                                No reason provided.
                                            </p>
                                        )}
                                    </div>
                                </div>
                            </>
                        )}

                        {/* Form submissions */}
                        {hasFormAnswers && (
                            <>
                                <div className="h-px bg-border" />
                                <div className="flex flex-col gap-3">
                                    <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
                                        Form response
                                    </p>
                                    {booking.formAnswers!.map((answer) => (
                                        <div
                                            key={`${answer.fieldLabel}-${answer.fieldResponse}`}
                                            className="flex flex-col gap-0.5"
                                        >
                                            <p className="text-xs text-muted-foreground">
                                                {answer.fieldLabel}
                                            </p>
                                            <p className="text-sm font-medium">
                                                {answer.fieldResponse}
                                            </p>
                                        </div>
                                    ))}
                                </div>
                            </>
                        )}
                    </div>
                </div>

                {(isUpcoming || isCancelled) && (
                    <div className="flex gap-2 border-t bg-muted/40 px-6 py-4">
                        {isCancelled && (
                            <Button
                                type="button"
                                variant="outline"
                                className="flex-1 gap-2"
                                onClick={() => {
                                    navigate(`/reschedule/${booking.id}`);
                                    onClose();
                                }}
                            >
                                <RotateCcw className="size-4" />
                                Reschedule
                            </Button>
                        )}

                        {isUpcoming && (
                            <>
                                <Button
                                    type="button"
                                    variant="outline"
                                    className="flex-1"
                                    onClick={() => {
                                        onNoShow(booking);
                                        onClose();
                                    }}
                                >
                                    Mark no-show
                                </Button>
                                <Button
                                    type="button"
                                    variant="destructive"
                                    className="flex-1"
                                    onClick={() => {
                                        onCancel(booking);
                                        onClose();
                                    }}
                                >
                                    Cancel booking
                                </Button>
                            </>
                        )}
                    </div>
                )}
            </DialogContent>
        </Dialog>
    );
};

// --- Row ---------------------------------------------------------------------

const Row = ({
    icon,
    label,
    value,
}: {
    icon: React.ReactNode;
    label: string;
    value: string;
}) => (
    <div className="flex items-start gap-3">
        <span className="mt-0.5 text-muted-foreground">{icon}</span>
        <div>
            <p className="text-xs text-muted-foreground">{label}</p>
            <p className="font-medium text-foreground">{value}</p>
        </div>
    </div>
);
