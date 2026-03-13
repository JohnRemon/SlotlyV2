import { CalendarX } from "lucide-react";

interface BookingCancelProps {
    eventName: string;
}

export const BookingCancel = ({ eventName }: BookingCancelProps) => (
    <div className="flex flex-col items-center justify-center py-16 gap-4 text-center">
        <div className="flex size-14 items-center justify-center rounded-full bg-destructive/10 text-destructive ring-1 ring-destructive/20">
            <CalendarX className="size-7" />
        </div>
        <div>
            <p className="text-lg font-bold tracking-[-0.02em]">
                You're cancelled!
            </p>
            <p className="mt-1 text-sm text-muted-foreground">
                Your appointment for{" "}
                <span className="font-medium text-foreground">
                    {eventName}
                </span>{" "}
                has been cancelled.
            </p>
        </div>
    </div>
);
