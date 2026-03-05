import { X } from "lucide-react";
import { useState } from "react";
import type { Booking } from "../types/Booking";

interface CancelModalProps {
    booking: Booking | null;
    onConfirm: (
        id: number,
        attendeeEmail: string,
        reason: string,
    ) => Promise<void>;
    onClose: () => void;
}

export const CancelBookingModal = ({
    booking,
    onConfirm,
    onClose,
}: CancelModalProps) => {
    const [reason, setReason] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    if (!booking) return null;

    const handleConfirm = async () => {
        setIsLoading(true);
        try {
            await onConfirm(booking.id, booking.attendeeEmail, reason);
            onClose();
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <dialog className="modal modal-open">
            <div className="modal-box rounded-2xl max-w-sm p-0 overflow-hidden">
                <div className="flex items-center justify-between px-6 py-4 border-b border-base-300">
                    <h3 className="font-bold text-base">Cancel Booking</h3>
                    <button
                        className="btn btn-ghost btn-xs btn-circle"
                        onClick={onClose}
                    >
                        <X className="w-4 h-4" />
                    </button>
                </div>

                <div className="px-6 py-5 flex flex-col gap-4">
                    <p className="text-sm text-base-content/60">
                        You're cancelling the booking for{" "}
                        <span className="font-semibold text-base-content">
                            {booking.attendeeName}
                        </span>
                        . This cannot be undone.
                    </p>
                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium">
                            Reason (optional)
                        </label>
                        <textarea
                            className="textarea textarea-bordered w-full resize-none"
                            rows={3}
                            placeholder="Let the attendee know why..."
                            value={reason}
                            onChange={(e) => setReason(e.target.value)}
                        />
                    </div>
                </div>

                <div className="flex gap-2 px-6 py-4 border-t border-base-300">
                    <button
                        className="btn btn-ghost btn-sm flex-1"
                        onClick={onClose}
                    >
                        Keep
                    </button>
                    <button
                        className="btn btn-error btn-sm flex-1"
                        onClick={handleConfirm}
                        disabled={isLoading}
                    >
                        {isLoading ? (
                            <span className="loading loading-spinner loading-xs" />
                        ) : (
                            "Cancel booking"
                        )}
                    </button>
                </div>
            </div>
            <div className="modal-backdrop" onClick={onClose} />
        </dialog>
    );
};
