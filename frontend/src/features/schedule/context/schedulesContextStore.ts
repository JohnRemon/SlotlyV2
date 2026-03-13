import { createContext, useContext } from "react";
import type {
    ScheduleResponse,
    ScheduleRequest,
    UpdateScheduleRequest,
} from "../types/Schedule";

export interface SchedulesContextValue {
    schedules: ScheduleResponse[];
    isLoading: boolean;
    create: (request: ScheduleRequest) => Promise<ScheduleResponse>;
    update: (request: UpdateScheduleRequest, id: string) => Promise<void>;
    remove: (id: string) => Promise<void>;
    setDefault: (id: string) => Promise<ScheduleResponse>;
    updateLocal: (schedule: ScheduleResponse) => void;
}

export const SchedulesContext = createContext<
    SchedulesContextValue | undefined
>(undefined);

export const useSchedulesContext = () => {
    const context = useContext(SchedulesContext);
    if (!context) {
        throw new Error(
            "useSchedulesContext must be used within SchedulesProvider",
        );
    }
    return context;
};
