import { Loader2Icon, XIcon } from "lucide-react";
import { useState } from "react";
import type { BookingResponse } from "../types/Booking";

import { Button } from "@/components/ui/button";
import {
    Dialog,
    DialogClose,
    DialogContent,
    DialogTitle,
} from "@/components/ui/dialog";
import { Textarea } from "@/components/ui/textarea";

interface CancelModalProps {
    booking: BookingResponse | null;
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
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Dialog open onOpenChange={(open) => !open && onClose()}>
            <DialogContent
                showCloseButton={false}
                className="gap-0 overflow-hidden p-0 sm:max-w-sm"
            >
                <div className="flex items-center justify-between border-b px-6 py-4">
                    <DialogTitle>Cancel booking</DialogTitle>
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

                <div className="px-6 py-5">
                    <div className="flex flex-col gap-4">
                        <p className="text-sm text-muted-foreground">
                            You're cancelling the booking for{" "}
                            <span className="font-semibold text-foreground">
                                {booking.attendeeName}
                            </span>
                            . This cannot be undone.
                        </p>

                        <div className="grid gap-2">
                            <label
                                htmlFor="cancel-booking-reason"
                                className="text-sm font-medium"
                            >
                                Reason (optional)
                            </label>
                            <Textarea
                                id="cancel-booking-reason"
                                rows={3}
                                placeholder="Let the attendee know why..."
                                value={reason}
                                onChange={(e) => setReason(e.target.value)}
                            />
                        </div>
                    </div>
                </div>

                <div className="flex gap-2 border-t bg-muted/40 px-6 py-4">
                    <Button
                        type="button"
                        variant="ghost"
                        className="flex-1"
                        onClick={onClose}
                    >
                        Keep booking
                    </Button>
                    <Button
                        type="button"
                        variant="destructive"
                        className="flex-1"
                        onClick={handleConfirm}
                        disabled={isLoading}
                    >
                        {isLoading ? (
                            <Loader2Icon className="size-4 animate-spin" />
                        ) : (
                            "Cancel booking"
                        )}
                    </Button>
                </div>
            </DialogContent>
        </Dialog>
    );
};
