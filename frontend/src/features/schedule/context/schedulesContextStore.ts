import { createContext, useContext } from "react";
import type {
    Schedule,
    ScheduleRequest,
    UpdateScheduleRequest,
} from "../types/Schedule";

export interface SchedulesContextValue {
    schedules: Schedule[];
    isLoading: boolean;
    create: (request: ScheduleRequest) => Promise<Schedule>;
    update: (request: UpdateScheduleRequest, id: string) => Promise<void>;
    remove: (id: string) => Promise<void>;
    setDefault: (id: string) => Promise<Schedule>;
    updateLocal: (schedule: Schedule) => void;
}

export const SchedulesContext = createContext<SchedulesContextValue | undefined>(
    undefined,
);

export const useSchedulesContext = () => {
    const context = useContext(SchedulesContext);
    if (!context) {
        throw new Error(
            "useSchedulesContext must be used within SchedulesProvider",
        );
    }
    return context;
};
