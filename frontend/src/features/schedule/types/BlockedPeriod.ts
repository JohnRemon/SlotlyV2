export interface BlockedPeriodResponse {
    id: string;
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
