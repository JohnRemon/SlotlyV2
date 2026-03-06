import { useEffect, useState } from "react";
import type { Booking } from "../types/Booking";
import {
    cancelBooking,
    getBookings,
    noShow as markAsNoShow,
} from "../api/BookingsApi";
import toast from "react-hot-toast";

export const useBookings = () => {
    const [bookings, setBookings] = useState<Booking[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        getBookings()
            .then(setBookings)
            .catch((error) => toast.error(error.response?.data?.message))
            .finally(() => setIsLoading(false));
    }, []);

    const cancel = async (
        id: number,
        attendeeEmail: string,
        reason: string,
    ) => {
        await cancelBooking(id, attendeeEmail, reason);
        setBookings((prev) =>
            prev.map((booking) =>
                booking.id === id
                    ? {
                          ...booking,
                          bookingStatus: "CANCELLED",
                          cancellationReason: reason,
                      }
                    : booking,
            ),
        );
    };

    const noShow = async (id: number) => {
        await markAsNoShow(id);

        setBookings((prevBookings) =>
            prevBookings.map((booking) =>
                booking.id === id
                    ? {
                          ...booking,
                          bookingStatus: "NO_SHOW",
                      }
                    : booking,
            ),
        );
    };

    return { bookings, isLoading, cancel, noShow };
};
