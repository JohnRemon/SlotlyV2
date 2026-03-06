import {
    Ban,
    Calendar,
    Clock,
    FileText,
    Mail,
    RotateCcw,
    X,
} from "lucide-react";
import { useNavigate } from "react-router";
import type { Booking } from "../types/Booking";
import { isPast, formatDate, formatTime } from "../utils/DateUtils";

// ─── Props ───────────────────────────────────────────────────────────────────

interface BookingDetailModalProps {
    booking: Booking | null;
    onClose: () => void;
    onCancel: (booking: Booking) => void;
    onNoShow: (booking: Booking) => void;
}

// ─── Component ───────────────────────────────────────────────────────────────

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
        <dialog className="modal modal-open">
            <div className="modal-box rounded-2xl max-w-lg w-full p-0 overflow-hidden max-h-[90vh] flex flex-col">
                {/* ── Header ── */}
                <div className="flex items-center justify-between px-6 py-4 border-b border-base-300 shrink-0">
                    <h3 className="font-bold text-base">Booking Details</h3>
                    <button
                        className="btn btn-ghost btn-xs btn-circle"
                        onClick={onClose}
                    >
                        <X className="w-4 h-4" />
                    </button>
                </div>

                {/* ── Scrollable Body ── */}
                <div className="flex-1 overflow-y-auto px-6 py-5 flex flex-col gap-5">
                    {/* Attendee */}
                    <div className="flex items-center gap-3">
                        <div className="w-11 h-11 rounded-full bg-primary/10 text-primary flex items-center justify-center font-bold shrink-0">
                            {booking.attendeeName[0].toUpperCase()}
                        </div>
                        <div>
                            <p className="font-semibold text-sm">
                                {booking.attendeeName}
                            </p>
                            <p className="text-xs text-base-content/50 flex items-center gap-1">
                                <Mail className="w-3 h-3" />{" "}
                                {booking.attendeeEmail}
                            </p>
                        </div>
                    </div>

                    <div className="divider my-0" />

                    {/* Booking Details */}
                    <div className="flex flex-col gap-3 text-sm">
                        <Row
                            icon={<Calendar className="w-4 h-4" />}
                            label="Date"
                            value={formatDate(booking.startTime)}
                        />
                        <Row
                            icon={<Clock className="w-4 h-4" />}
                            label="Time"
                            value={`${formatTime(booking.startTime)} – ${formatTime(booking.endTime)}`}
                        />
                        <Row
                            icon={<FileText className="w-4 h-4" />}
                            label="Event"
                            value={booking.eventName}
                        />
                        {booking.notes && (
                            <Row
                                icon={<FileText className="w-4 h-4" />}
                                label="Notes"
                                value={booking.notes}
                            />
                        )}
                    </div>

                    {/* Cancellation Info */}
                    {isCancelled && (
                        <>
                            <div className="divider my-0" />
                            <div className="bg-error/5 border border-error/20 rounded-xl px-4 py-3 flex flex-col gap-2">
                                <p className="text-xs font-semibold text-error uppercase tracking-wide">
                                    Cancellation
                                </p>
                                {booking.cancelledAt && (
                                    <Row
                                        icon={<Clock className="w-4 h-4" />}
                                        label="Cancelled at"
                                        value={formatDate(booking.cancelledAt)}
                                    />
                                )}
                                {booking.cancellationReason ? (
                                    <Row
                                        icon={<Ban className="w-4 h-4" />}
                                        label="Reason"
                                        value={booking.cancellationReason}
                                    />
                                ) : (
                                    <p className="text-xs text-base-content/40">
                                        No reason provided.
                                    </p>
                                )}
                            </div>
                        </>
                    )}

                    {/* Form Submissions */}
                    {hasFormAnswers && (
                        <>
                            <div className="divider my-0" />
                            <div className="flex flex-col gap-3">
                                <p className="text-xs font-semibold text-base-content/40 uppercase tracking-wide">
                                    Form Response
                                </p>
                                {booking.formAnswers!.map((answer, i) => (
                                    <div
                                        key={i}
                                        className="flex flex-col gap-0.5"
                                    >
                                        <p className="text-xs text-base-content/40">
                                            {answer.fieldLabel}
                                        </p>
                                        <p className="text-sm text-base-content font-medium">
                                            {answer.fieldAnswer}
                                        </p>
                                    </div>
                                ))}
                            </div>
                        </>
                    )}
                </div>

                {/* ── Footer ── */}
                {(isUpcoming || isCancelled) && (
                    <div className="flex gap-2 px-6 py-4 border-t border-base-300 shrink-0">
                        {isCancelled && (
                            <button
                                className="btn btn-outline btn-sm flex-1 gap-2"
                                onClick={() => {
                                    navigate(`/reschedule/${booking.id}`);
                                    onClose();
                                }}
                            >
                                <RotateCcw className="w-4 h-4" /> Reschedule
                            </button>
                        )}
                        {isUpcoming && (
                            <>
                                <button
                                    className="btn btn-outline btn-sm flex-1"
                                    onClick={() => {
                                        onNoShow(booking);
                                        onClose();
                                    }}
                                >
                                    Mark no-show
                                </button>
                                <button
                                    className="btn btn-error btn-sm flex-1"
                                    onClick={() => {
                                        onCancel(booking);
                                        onClose();
                                    }}
                                >
                                    Cancel booking
                                </button>
                            </>
                        )}
                    </div>
                )}
            </div>
            <div className="modal-backdrop" onClick={onClose} />
        </dialog>
    );
};

// ─── Row ─────────────────────────────────────────────────────────────────────

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
        <span className="text-base-content/40 mt-0.5">{icon}</span>
        <div>
            <p className="text-xs text-base-content/40">{label}</p>
            <p className="text-base-content font-medium">{value}</p>
        </div>
    </div>
);
