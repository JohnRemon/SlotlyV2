import type { User } from "../../profile/types/User";

export interface BlockedPeriod {
    id: string;
    user: User;
    startTime: string;
    endTime: string;
    reason?: string;
    isRecurring: string;
}

export interface BlockedPeriodRequest {
    startTime: string;
    endTime: string;
    reason?: string;
    isRecurring: string;
}

export interface BlockedPeriodResponse {
    startTime: string;
    endTime: string;
    reason?: string;
    isRecurring: string;
}
