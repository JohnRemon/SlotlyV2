import { CalendarCheck } from "lucide-react";

interface BookingSuccessProps {
    eventName: string;
}

export const BookingSuccess = ({ eventName }: BookingSuccessProps) => (
    <div className="flex flex-col items-center justify-center py-16 gap-4 text-center">
        <div className="w-14 h-14 rounded-full bg-success/10 text-success flex items-center justify-center">
            <CalendarCheck className="w-7 h-7" />
        </div>
        <div>
            <p className="text-lg font-bold text-base-content">
                You're booked!
            </p>
            <p className="text-sm text-base-content/50 mt-1">
                Your appointment for{" "}
                <span className="font-medium text-base-content">
                    {eventName}
                </span>{" "}
                has been confirmed. Check your email for details.
            </p>
        </div>
    </div>
);
