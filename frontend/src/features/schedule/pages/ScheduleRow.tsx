import axios from "axios";
import { Clock, Trash2 } from "lucide-react";
import { useState } from "react";
import toast from "react-hot-toast";
import { useNavigate } from "react-router";
import type { Schedule } from "../types/Schedule";

const DAY_NAMES = ["", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

interface ScheduleRowProps {
    schedule: Schedule;
    onDelete: (id: string) => Promise<void>;
}

const ScheduleRow = ({ schedule, onDelete }: ScheduleRowProps) => {
    const navigate = useNavigate();
    const [isDeleting, setIsDeleting] = useState(false);

    const availableDays = schedule.dailySchedules.filter((d) => d.isAvailable);
    const summary =
        availableDays.length === 0
            ? "No available days"
            : availableDays.map((d) => DAY_NAMES[d.dayOfWeek]).join(", ");

    const handleDelete = async (e: React.MouseEvent) => {
        e.stopPropagation();
        setIsDeleting(true);
        try {
            await onDelete(schedule.id);
            toast.success("Schedule deleted");
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(
                    error.response?.data?.message ?? "Failed to delete",
                );
            } else {
                toast.error("Something went wrong");
            }
        } finally {
            setIsDeleting(false);
        }
    };

    return (
        <div
            onClick={() => navigate(`/schedules/${schedule.id}`)}
            className="flex items-center justify-between px-4 py-3.5 border border-base-300 rounded-xl hover:bg-base-200/50 cursor-pointer transition-colors"
        >
            <div className="flex items-center gap-3">
                <div className="w-8 h-8 rounded-lg bg-primary/10 flex items-center justify-center shrink-0">
                    <Clock className="w-4 h-4 text-primary" />
                </div>
                <div>
                    <div className="flex items-center gap-2">
                        <span className="text-sm font-medium">
                            {schedule.name}
                        </span>
                        {schedule.isDefault && (
                            <span className="text-xs bg-primary/10 text-primary px-1.5 py-0.5 rounded-md font-medium">
                                Default
                            </span>
                        )}
                    </div>
                    <p className="text-xs text-base-content/40 mt-0.5">
                        {summary}
                    </p>
                </div>
            </div>

            <button
                type="button"
                disabled={isDeleting}
                onClick={handleDelete}
                className="btn btn-ghost btn-xs btn-square text-error hover:bg-error/10 rounded-lg"
            >
                {isDeleting ? (
                    <span className="loading loading-spinner loading-xs" />
                ) : (
                    <Trash2 className="w-3.5 h-3.5" />
                )}
            </button>
        </div>
    );
};

export default ScheduleRow;
