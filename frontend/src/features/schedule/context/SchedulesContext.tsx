import { useEffect, useMemo, useState } from "react";
import toast from "react-hot-toast";
import { Outlet } from "react-router";
import {
    createSchedule,
    deleteSchedule,
    getSchedules,
    updateDefaultSchedule,
    updateSchedule,
} from "../api/SchedulesApi";
import type {
    Schedule,
    ScheduleRequest,
    UpdateScheduleRequest,
} from "../types/Schedule";
import { sortSchedules, upsertSchedule } from "../utils/scheduleState";
import { SchedulesContext } from "./schedulesContextStore";

export const SchedulesProvider = () => {
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
        setSchedules((prev) => sortSchedules([newSchedule, ...prev]));
        return newSchedule;
    };

    const update = async (request: UpdateScheduleRequest, id: string) => {
        const updatedSchedule = await updateSchedule(request, id);
        setSchedules((prev) =>
            sortSchedules(
                prev.map((schedule) =>
                    schedule.id === id ? updatedSchedule : schedule,
                ),
            ),
        );
    };

    const setDefault = async (id: string) => {
        const updated = await updateDefaultSchedule(id);
        setSchedules((prev) =>
            sortSchedules(
                prev.map((schedule) => ({
                    ...schedule,
                    isDefault: schedule.id === id,
                })),
            ),
        );
        return updated;
    };

    const remove = async (id: string) => {
        await deleteSchedule(id);
        setSchedules((prev) =>
            sortSchedules(prev.filter((schedule) => schedule.id !== id)),
        );
    };

    const updateLocal = (schedule: Schedule) => {
        setSchedules((prev) => sortSchedules(upsertSchedule(prev, schedule)));
    };

    const value = useMemo(
        () => ({
            schedules,
            isLoading,
            create,
            update,
            remove,
            setDefault,
            updateLocal,
        }),
        [schedules, isLoading],
    );

    return (
        <SchedulesContext.Provider value={value}>
            <Outlet />
        </SchedulesContext.Provider>
    );
};

export default SchedulesProvider;
