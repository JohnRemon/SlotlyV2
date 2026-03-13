import { useCallback, useEffect, useMemo, useState } from "react";
import { toast } from "sonner";
import { Outlet } from "react-router";
import { SchedulesApi } from "../api/SchedulesApi";
import type {
    ScheduleResponse,
    ScheduleRequest,
    UpdateScheduleRequest,
} from "../types/Schedule";
import { sortSchedules, upsertSchedule } from "../utils/scheduleState";
import { SchedulesContext } from "./schedulesContextStore";

export const SchedulesProvider = () => {
    const [schedules, setSchedules] = useState<ScheduleResponse[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        SchedulesApi.getAll()
            .then((response) =>
                setSchedules(sortSchedules(response.data.content)),
            )
            .catch((error) => toast.error(error.response?.data?.message))
            .finally(() => setIsLoading(false));
    }, []);

    const create = useCallback(async (request: ScheduleRequest) => {
        const newSchedule = (await SchedulesApi.create(request)).data.data;
        setSchedules((prev) => sortSchedules([newSchedule, ...prev]));
        return newSchedule;
    }, []);

    const update = useCallback(async (request: UpdateScheduleRequest, id: string) => {
        const updatedSchedule = (await SchedulesApi.updateDays(id, request)).data
            .data;
        setSchedules((prev) =>
            sortSchedules(
                prev.map((schedule) =>
                    schedule.id === id ? updatedSchedule : schedule,
                ),
            ),
        );
    }, []);

    const setDefault = useCallback(async (id: string) => {
        const updated = (await SchedulesApi.updateDefault(id)).data.data;
        setSchedules((prev) =>
            sortSchedules(
                prev.map((schedule) =>
                    schedule.id === updated.id
                        ? updated
                        : { ...schedule, isDefault: false },
                ),
            ),
        );
        return updated;
    }, []);

    const remove = useCallback(async (id: string) => {
        await SchedulesApi.delete(id);
        setSchedules((prev) =>
            sortSchedules(prev.filter((schedule) => schedule.id !== id)),
        );
    }, []);

    const updateLocal = useCallback((schedule: ScheduleResponse) => {
        setSchedules((prev) => sortSchedules(upsertSchedule(prev, schedule)));
    }, []);

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
        [create, isLoading, remove, schedules, setDefault, update, updateLocal],
    );

    return (
        <SchedulesContext.Provider value={value}>
            <Outlet />
        </SchedulesContext.Provider>
    );
};

export default SchedulesProvider;
