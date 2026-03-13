import type { ScheduleResponse } from "../types/Schedule";

export const sortSchedules = (items: ScheduleResponse[]) =>
    [...items].sort((a, b) => (b.isDefault ? 1 : 0) - (a.isDefault ? 1 : 0));

export const upsertSchedule = (
    items: ScheduleResponse[],
    schedule: ScheduleResponse,
) =>
    items.some((item) => item.id === schedule.id)
        ? items.map((item) => (item.id === schedule.id ? schedule : item))
        : [schedule, ...items];
