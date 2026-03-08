import { useEffect, useState } from "react";
import type {
    Schedule,
    ScheduleRequest,
    UpdateScheduleRequest,
} from "../types/Schedule";
import {
    createSchedule,
    deleteSchedule,
    getSchedules,
    updateSchedule,
} from "../api/SchedulesApi";
import toast from "react-hot-toast";

export const useSchedules = () => {
    const [schedules, setSchedules] = useState<Schedule[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        getSchedules()
            .then(setSchedules)
            .catch((error) => toast.error(error.response?.data?.message))
            .finally(() => setIsLoading(false));
    }, []);

    const create = async (request: ScheduleRequest) => {
        const newSchedule = await createSchedule(request);
        setSchedules((prev) => [newSchedule, ...prev]);
        return newSchedule;
    };

    const update = async (request: UpdateScheduleRequest, id: string) => {
        const updatedSchedule = await updateSchedule(request, id);
        setSchedules((prev) =>
            prev.map((schedule) =>
                schedule.id === id ? updatedSchedule : schedule,
            ),
        );
    };

    const remove = async (id: string) => {
        await deleteSchedule(id);
        setSchedules((prev) => prev.filter((schedule) => schedule.id !== id));
    };

    return { schedules, isLoading, create, update, remove };
};
