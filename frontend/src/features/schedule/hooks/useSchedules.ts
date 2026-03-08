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
    updateDefaultSchedule,
    updateSchedule,
} from "../api/SchedulesApi";
import toast from "react-hot-toast";
import { sortSchedules, upsertSchedule } from "../utils/scheduleState";

export const useSchedules = () => {
    const [schedules, setSchedules] = useState<Schedule[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        getSchedules()
            .then((data) => setSchedules(sortSchedules(data)))
            .catch((error) => toast.error(error.response?.data?.message))
            .finally(() => setIsLoading(false));
    }, []);

    const create = async (request: ScheduleRequest) => {
        const newSchedule = await createSchedule(request);
        setSchedules((prev) => sortSchedules(upsertSchedule(prev, newSchedule)));
        return newSchedule;
    };

    const update = async (request: UpdateScheduleRequest, id: string) => {
        const updatedSchedule = await updateSchedule(request, id);
        setSchedules((prev) => sortSchedules(upsertSchedule(prev, updatedSchedule)));
    };

    const setDefault = async (id: string) => {
        const updated = await updateDefaultSchedule(id);
        setSchedules((prev) =>
            prev
                .map((s) => ({ ...s, isDefault: s.id === id }))
                .sort((a, b) => (b.isDefault ? 1 : 0) - (a.isDefault ? 1 : 0)),
        );
        return updated;
    };

    const remove = async (id: string) => {
        await deleteSchedule(id);
        setSchedules((prev) =>
            prev
                .filter((s) => s.id !== id)
                .sort((a, b) => (b.isDefault ? 1 : 0) - (a.isDefault ? 1 : 0)),
        );
    };

    return { schedules, isLoading, create, update, remove, setDefault };
};
