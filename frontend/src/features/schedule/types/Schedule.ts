export interface DailyScheduleRequest {
    dayOfWeek: number;
    startTime: string | null;
    endTime: string | null;
    isAvailable: boolean;
}

export interface DailyScheduleResponse {
    dayOfWeek: number;
    startTime: string | null;
    endTime: string | null;
    isAvailable: boolean;
}

export interface ScheduleRequest {
    name: string;
}

export interface ScheduleResponse {
    id: string;
    name: string;
    isDefault: boolean;
    dailySchedules: DailyScheduleResponse[];
}

export interface UpdateScheduleRequest {
    days: DailyScheduleRequest[];
}
