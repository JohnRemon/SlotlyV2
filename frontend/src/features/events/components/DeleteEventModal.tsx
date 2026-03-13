import type { EventResponse } from "../types/Event";

import ConfirmDialog from "@/components/common/ConfirmDialog";

interface DeleteEventModalProps {
    event: EventResponse | null;
    onConfirm: (event: EventResponse) => Promise<void>;
    onClose: () => void;
}

export const DeleteEventModal = ({
    event,
    onConfirm,
    onClose,
}: DeleteEventModalProps) => {
    if (!event) return null;

    return (
        <ConfirmDialog
            open
            onOpenChange={(next) => {
                if (!next) onClose();
            }}
            title="Delete event"
            description={
                <span>
                    Are you sure you want to delete <b>{event.eventName}</b>? This will also cancel all existing bookings.
                </span>
            }
            confirmLabel="Delete"
            cancelLabel="Cancel"
            confirmVariant="destructive"
            onConfirm={() => onConfirm(event)}
        />
    );
};
