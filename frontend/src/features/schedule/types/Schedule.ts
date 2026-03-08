import type { User } from "../../profile/types/User";

export interface DailyScheduleRequest {
    dayOfWeek: number;
    startTime: string;
    endTime: string;
    isAvailable: boolean;
}

export interface DailyScheduleResponse {
    dayOfWeek: number;
    startTime: string;
    endTime: string;
    isAvailable: boolean;
}

export interface ScheduleRequest {
    name: string;
}

export interface UpdateScheduleRequest {
    name: string;
    days: DailyScheduleRequest[];
    isDefault: boolean;
}

export interface ScheduleResponse {
    id: string;
    name: string;
    dailySchedules: DailyScheduleResponse[];
    isDefault: boolean;
}

export interface Schedule {
    id: string;
    user: User;
    name: string;
    dailySchedules: DailySchedule[];
    isDefault: boolean;
}

export interface DailySchedule {
    dayOfWeek: number;
    startTime: string;
    endTime: string;
    isAvailable: boolean;
}
