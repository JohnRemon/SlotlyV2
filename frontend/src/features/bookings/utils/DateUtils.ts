export const formatDate = (iso: string): string => {
    return new Date(iso).toLocaleDateString("en-US", {
        weekday: "short",
        month: "short",
        day: "numeric",
        year: "numeric",
    });
};

export const formatTime = (iso: string): string => {
    return new Date(iso).toLocaleTimeString("en-US", {
        hour: "2-digit",
        minute: "2-digit",
    });
};

export const isPast = (endTime: string) => new Date(endTime) < new Date();
