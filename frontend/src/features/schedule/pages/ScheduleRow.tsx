import axios from "axios";
import { Clock, Trash2 } from "lucide-react";
import { useState } from "react";
import toast from "react-hot-toast";
import { useNavigate } from "react-router";
import {
    getEventsBySchedule,
    updateSchedule as updateEventSchedule,
} from "../../events/api/EventsApi";
import type { Event } from "../../events/types/Event";
import { useSchedulesContext } from "../context/schedulesContextStore";
import type { Schedule } from "../types/Schedule";
import SelectNewDefaultModal from "../components/SelectNewDefaultScheduleModal";
import AffectedEventsModal from "../components/AffectedEventsModal";

const DAY_NAMES = ["", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

interface ScheduleRowProps {
    schedule: Schedule;
    onDelete: (id: string) => Promise<void>;
}

// ── ScheduleRow ───────────────────────────────────────────────────────────────
const ScheduleRow = ({ schedule, onDelete }: ScheduleRowProps) => {
    const navigate = useNavigate();
    const { schedules, setDefault } = useSchedulesContext();

    const [isDeleting, setIsDeleting] = useState(false);
    const [affectedEvents, setAffectedEvents] = useState<Event[] | null>(null);
    const [step, setStep] = useState<"idle" | "newDefault" | "affectedEvents">(
        "idle",
    );

    const availableDays = schedule.dailySchedules.filter((d) => d.isAvailable);
    const summary =
        availableDays.length === 0
            ? "No available days"
            : availableDays.map((d) => DAY_NAMES[d.dayOfWeek]).join(", ");

    const otherSchedules = schedules.filter((s) => s.id !== schedule.id);

    const handleDeleteClick = async (e: React.MouseEvent) => {
        e.stopPropagation();

        if (schedules.length === 1) {
            toast.error("Cannot delete the only schedule");
            return;
        }

        setIsDeleting(true);
        try {
            const events = await getEventsBySchedule(schedule.id);
            setAffectedEvents(events);
            setStep(schedule.isDefault ? "newDefault" : "affectedEvents");
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(error.response?.data?.message);
            } else {
                toast.error("Something went wrong");
            }
        } finally {
            setIsDeleting(false);
        }
    };

    const handleNewDefaultConfirm = async (newDefaultId: string) => {
        try {
            await setDefault(newDefaultId);
            setStep("affectedEvents");
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(error.response?.data?.message);
            } else {
                toast.error("Something went wrong");
            }
        }
    };

    const handleAffectedEventsConfirm = async (
        assignments: Record<string, string>,
    ) => {
        setIsDeleting(true);
        try {
            // Reassign each affected event one by one
            await Promise.all(
                Object.entries(assignments).map(([eventId, scheduleId]) =>
                    updateEventSchedule(Number(eventId), scheduleId),
                ),
            );
            await onDelete(schedule.id);
            toast.success("Schedule deleted");
            setStep("idle");
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(error.response?.data?.message);
            } else {
                toast.error("Something went wrong");
            }
        } finally {
            setIsDeleting(false);
        }
    };

    const handleClose = () => {
        setStep("idle");
        setAffectedEvents(null);
    };

    return (
        <>
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
                    onClick={handleDeleteClick}
                    className="btn btn-ghost btn-xs btn-square text-error hover:bg-error/10 rounded-lg"
                >
                    {isDeleting ? (
                        <span className="loading loading-spinner loading-xs" />
                    ) : (
                        <Trash2 className="w-3.5 h-3.5" />
                    )}
                </button>
            </div>

            {step === "newDefault" && (
                <SelectNewDefaultModal
                    schedules={otherSchedules}
                    onConfirm={handleNewDefaultConfirm}
                    onClose={handleClose}
                />
            )}

            {step === "affectedEvents" && affectedEvents !== null && (
                <AffectedEventsModal
                    events={affectedEvents}
                    schedules={otherSchedules}
                    currentScheduleId={schedule.id}
                    onConfirm={handleAffectedEventsConfirm}
                    onClose={handleClose}
                />
            )}
        </>
    );
};

export default ScheduleRow;
