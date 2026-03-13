import { Clock, Loader2Icon, Trash2 } from "lucide-react";
import { useState } from "react";
import { useNavigate } from "react-router";
import { toast } from "sonner";
import { EventsApi } from "../../events/api/EventsApi";
import type { EventResponse } from "../../events/types/Event";
import AffectedEventsModal from "../components/AffectedEventsModal";
import SelectNewDefaultModal from "../components/SelectNewDefaultScheduleModal";
import { useSchedulesContext } from "../context/schedulesContextStore";
import type { ScheduleResponse } from "../types/Schedule";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useApiError } from "@/hooks/useApiError";

const DAY_NAMES = ["", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

interface ScheduleRowProps {
    schedule: ScheduleResponse;
    onDelete: (id: string) => Promise<void>;
}

// -- ScheduleRow ---------------------------------------------------------------
const ScheduleRow = ({ schedule, onDelete }: ScheduleRowProps) => {
    const navigate = useNavigate();
    const handleError = useApiError();
    const { schedules, setDefault } = useSchedulesContext();

    const [isDeleting, setIsDeleting] = useState(false);
    const [affectedEvents, setAffectedEvents] = useState<
        EventResponse[] | null
    >(null);
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
            const res = await EventsApi.getByScheduleId(schedule.id);
            setAffectedEvents(res.data.content);
            setStep(schedule.isDefault ? "newDefault" : "affectedEvents");
        } catch (error) {
            handleError(error);
        } finally {
            setIsDeleting(false);
        }
    };

    const handleNewDefaultConfirm = async (newDefaultId: string) => {
        try {
            await setDefault(newDefaultId);
            setStep("affectedEvents");
        } catch (error) {
            handleError(error);
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
                    EventsApi.updateSchedule(Number(eventId), scheduleId),
                ),
            );
            await onDelete(schedule.id);
            toast.success("Schedule deleted");
            setStep("idle");
        } catch (error) {
            handleError(error);
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
            <div className="flex items-center justify-between rounded-xl bg-card px-4 py-3.5 ring-1 ring-foreground/10 transition-colors hover:bg-muted/20">
                <button
                    type="button"
                    onClick={() => navigate(`/schedules/${schedule.id}`)}
                    className="flex min-w-0 flex-1 items-center gap-3 text-left"
                >
                    <div className="flex size-8 shrink-0 items-center justify-center rounded-lg bg-primary/10 ring-1 ring-primary/15">
                        <Clock className="size-4 text-primary" />
                    </div>
                    <div className="min-w-0">
                        <div className="flex items-center gap-2">
                            <span className="truncate text-sm font-medium">
                                {schedule.name}
                            </span>
                            {schedule.isDefault && (
                                <Badge variant="secondary">Default</Badge>
                            )}
                        </div>
                        <p className="mt-0.5 text-xs text-muted-foreground">
                            {summary}
                        </p>
                    </div>
                </button>

                <Button
                    type="button"
                    variant="ghost"
                    size="icon-sm"
                    disabled={isDeleting}
                    onClick={handleDeleteClick}
                    className="text-destructive hover:bg-destructive/10 hover:text-destructive"
                    aria-label={`Delete schedule ${schedule.name}`}
                >
                    {isDeleting ? (
                        <Loader2Icon className="size-4 animate-spin" />
                    ) : (
                        <Trash2 className="size-4" />
                    )}
                </Button>
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
