import { Trash2, X } from "lucide-react";
import type { Event } from "../types/Event";
import { useState } from "react";

interface DeleteEventModalProps {
    event: Event | null;
    onConfirm: (event: Event) => Promise<void>;
    onClose: () => void;
}

export const DeleteEventModal = ({
    event,
    onConfirm,
    onClose,
}: DeleteEventModalProps) => {
    const [isLoading, setIsLoading] = useState(false);

    if (!event) return null;

    const handleConfirm = async () => {
        setIsLoading(true);
        try {
            await onConfirm(event);
            onClose();
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <dialog className="modal modal-open">
            <div className="modal-box rounded-2xl max-w-sm p-0 overflow-hidden">
                <div className="flex items-center justify-between px-6 py-4 border-b border-base-300">
                    <h3 className="font-bold text-base">Delete Event</h3>
                    <button
                        className="btn btn-ghost btn-xs btn-circle"
                        onClick={onClose}
                    >
                        <X className="w-4 h-4" />
                    </button>
                </div>
                <div className="px-6 py-5 flex flex-col gap-2">
                    <p className="text-sm text-base-content/60">
                        Are you sure you want to delete{" "}
                        <span className="font-semibold text-base-content">
                            {event.eventName}
                        </span>
                        ? This will also cancel all existing bookings.
                    </p>
                </div>
                <div className="flex gap-2 px-6 py-4 border-t border-base-300">
                    <button
                        className="btn btn-outline btn-sm flex-1"
                        onClick={onClose}
                    >
                        Cancel
                    </button>
                    <button
                        className="btn btn-error btn-sm flex-1 gap-2"
                        onClick={handleConfirm}
                        disabled={isLoading}
                    >
                        {isLoading ? (
                            <span className="loading loading-spinner loading-xs" />
                        ) : (
                            <>
                                <Trash2 className="w-4 h-4" /> Delete
                            </>
                        )}
                    </button>
                </div>
            </div>
            <div className="modal-backdrop" onClick={onClose} />
        </dialog>
    );
};
