import { useEffect, useState } from "react";
import {
    createEvent,
    deleteEvent,
    getEvents,
    updateEvent,
} from "../api/EventsApi";
import toast from "react-hot-toast";
import type { Event, EventRequest } from "../types/Event";

export const useEvents = () => {
    const [events, setEvents] = useState<Event[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        getEvents()
            .then(setEvents)
            .catch((error) => toast.error(error.response?.data?.message))
            .finally(() => setIsLoading(false));
    }, []);

    const create = async (payload: EventRequest) => {
        const newEvent = await createEvent(payload);
        setEvents((prev) => [newEvent, ...prev]);
    };

    const update = async (payload: EventRequest, id: number) => {
        const updatedEvent = await updateEvent(payload, id);
        setEvents((prev) => prev.map((e) => (e.id === id ? updatedEvent : e)));
    };

    const remove = async (id: number) => {
        await deleteEvent(id);
        setEvents((prev) => prev.filter((event) => event.id !== id));
    };

    return { events, isLoading, create, update, remove };
};
