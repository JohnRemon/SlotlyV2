import type { Schedule } from "../types/Schedule";

export const sortSchedules = (items: Schedule[]) =>
    [...items].sort((a, b) => (b.isDefault ? 1 : 0) - (a.isDefault ? 1 : 0));

export const upsertSchedule = (items: Schedule[], schedule: Schedule) =>
    items.some((item) => item.id === schedule.id)
        ? items.map((item) => (item.id === schedule.id ? schedule : item))
        : [schedule, ...items];
