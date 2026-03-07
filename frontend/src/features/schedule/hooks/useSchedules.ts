import { useEffect, useState } from "react";
import type { Schedule } from "../types/Schedule";

export const useEvents = () => {
    const [schedules, setSchedules] = useState<Schedule[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {});
};
