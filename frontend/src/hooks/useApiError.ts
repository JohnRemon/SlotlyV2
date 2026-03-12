import axios from "axios";
import { useCallback } from "react";
import { toast } from "sonner";

export const useApiError = () => {
    return useCallback((error: unknown, fallback = "Something went wrong") => {
        if (axios.isAxiosError(error)) {
            const message = error.response?.data?.message ?? fallback;
            toast.error(message);
        }
    }, []);
};
