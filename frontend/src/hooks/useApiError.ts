import axios from "axios";
import { toast } from "sonner";

export const useApiError = () => {
    return (error: unknown, fallback = "Something went wrong") => {
        if (axios.isAxiosError(error)) {
            toast.error(error.response?.data?.message ?? fallback);
        }
    };
};
