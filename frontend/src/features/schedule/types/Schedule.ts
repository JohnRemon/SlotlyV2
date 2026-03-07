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
    isAvailable: string;
}

export interface ScheduleRequest {
    days: DailyScheduleRequest[];
}

export interface Schedule {
    id: string;
    user: User;
    dayOfWeek: number;
    startTime: string;
    endTime: string;
    isAvailable: string;
}
