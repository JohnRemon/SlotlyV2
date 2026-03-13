import { CalendarCheck } from "lucide-react";

interface BookingSuccessProps {
    eventName: string;
}

export const BookingSuccess = ({ eventName }: BookingSuccessProps) => (
    <div className="flex flex-col items-center justify-center py-16 gap-4 text-center">
        <div className="flex size-14 items-center justify-center rounded-full bg-primary/10 text-primary ring-1 ring-primary/20">
            <CalendarCheck className="size-7" />
        </div>
        <div>
            <p className="text-lg font-bold tracking-[-0.02em]">
                You're booked!
            </p>
            <p className="mt-1 text-sm text-muted-foreground">
                Your appointment for{" "}
                <span className="font-medium text-foreground">
                    {eventName}
                </span>{" "}
                has been confirmed. Check your email for details.
            </p>
        </div>
    </div>
);
