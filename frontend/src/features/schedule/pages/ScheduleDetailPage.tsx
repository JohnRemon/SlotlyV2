import {
    CheckIcon,
    ChevronLeft,
    Loader2Icon,
    PencilIcon,
    XIcon,
} from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { toast } from "sonner";
import { SchedulesApi } from "../api/SchedulesApi";
import { useSchedulesContext } from "../context/schedulesContextStore";
import type {
    DailyScheduleResponse,
    ScheduleResponse,
} from "../types/Schedule";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Switch } from "@/components/ui/switch";
import { useApiError } from "@/hooks/useApiError";

const DAY_NAMES = [
    "",
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday",
    "Sunday",
];

const DEFAULT_DAY_START = "09:00";
const DEFAULT_DAY_END = "17:00";

const toTimeInput = (time?: string | null) => (time ? time.slice(0, 5) : "");

const ScheduleDetailPage = () => {
    const { id } = useParams() as { id: string };
    const navigate = useNavigate();
    const handleError = useApiError();
    const { updateLocal } = useSchedulesContext();

    const [schedule, setSchedule] = useState<ScheduleResponse | null>(null);
    const [days, setDays] = useState<DailyScheduleResponse[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isSaving, setIsSaving] = useState(false);

    // Name editing
    const [editingName, setEditingName] = useState(false);
    const [draftName, setDraftName] = useState("");
    const [isRenaming, setIsRenaming] = useState(false);
    const nameInputRef = useRef<HTMLInputElement>(null);

    useEffect(() => {
        SchedulesApi.getById(id)
            .then((res) => {
                const s = res.data.data;
                setSchedule(s);
                setDays(
                    [...s.dailySchedules].sort(
                        (a, b) => a.dayOfWeek - b.dayOfWeek,
                    ),
                );
            })
            .catch((error) => handleError(error))
            .finally(() => setIsLoading(false));
    }, [id]);

    useEffect(() => {
        if (editingName) nameInputRef.current?.focus();
    }, [editingName]);

    const startEditing = () => {
        setDraftName(schedule?.name ?? "");
        setEditingName(true);
    };

    const cancelEditing = () => {
        setEditingName(false);
        setDraftName("");
    };

    const handleRenameSave = async () => {
        if (!draftName.trim() || !schedule) return;
        setIsRenaming(true);
        try {
            const updated = (
                await SchedulesApi.updateName(id, draftName.trim())
            ).data.data;
            setSchedule(updated);
            updateLocal(updated);
            setEditingName(false);
            toast.success("Name updated");
        } catch (error) {
            handleError(error);
        } finally {
            setIsRenaming(false);
        }
    };

    const updateDay = (
        dayOfWeek: number,
        patch: Partial<DailyScheduleResponse>,
    ) => {
        setDays((prev) =>
            prev.map((schedule) =>
                schedule.dayOfWeek === dayOfWeek
                    ? { ...schedule, ...patch }
                    : schedule,
            ),
        );
    };

    const handleSave = async () => {
        if (!schedule) return;

        const invalidAvailableDay = days.find(
            (day) => day.isAvailable && (!day.startTime || !day.endTime),
        );

        if (invalidAvailableDay) {
            toast.error("Available days must have both a start and end time");
            return;
        }

        setIsSaving(true);
        try {
            const updated = (
                await SchedulesApi.updateDays(id, {
                    days: days.map((schedule) => ({
                        dayOfWeek: schedule.dayOfWeek,
                        startTime: schedule.startTime,
                        endTime: schedule.endTime,
                        isAvailable: schedule.isAvailable,
                    })),
                })
            ).data.data;
            setSchedule(updated);
            updateLocal(updated);
            setDays(
                [...updated.dailySchedules].sort(
                    (a, b) => a.dayOfWeek - b.dayOfWeek,
                ),
            );
            toast.success("Schedule saved");
        } catch (error) {
            handleError(error);
        } finally {
            setIsSaving(false);
        }
    };

    if (isLoading)
        return (
            <div className="flex items-center justify-center h-64">
                <Loader2Icon className="size-5 animate-spin text-muted-foreground" />
            </div>
        );

    if (!schedule)
        return (
            <div className="flex items-center justify-center h-64 text-sm text-muted-foreground">
                Schedule not found.
            </div>
        );

    return (
        <div className="mx-auto flex w-full max-w-3xl flex-col gap-6 px-4 py-8">
            {/* Header */}
            <div className="flex items-center justify-between gap-4">
                <div className="flex items-center gap-3">
                    <Button
                        type="button"
                        variant="ghost"
                        size="icon"
                        className="rounded-full"
                        onClick={() => navigate(-1)}
                        aria-label="Back"
                    >
                        <ChevronLeft className="size-4" />
                    </Button>
                    <div>
                        <div className="flex items-center gap-2">
                            {editingName ? (
                                <div className="flex items-center gap-1.5">
                                    <Input
                                        ref={nameInputRef}
                                        type="text"
                                        className="h-8 w-56 text-base font-semibold"
                                        value={draftName}
                                        onChange={(e) =>
                                            setDraftName(e.target.value)
                                        }
                                        onKeyDown={(e) => {
                                            if (e.key === "Enter")
                                                handleRenameSave();
                                            if (e.key === "Escape")
                                                cancelEditing();
                                        }}
                                    />
                                    <Button
                                        type="button"
                                        variant="ghost"
                                        size="icon-sm"
                                        className="text-primary"
                                        disabled={isRenaming}
                                        onClick={handleRenameSave}
                                        aria-label="Save name"
                                    >
                                        {isRenaming ? (
                                            <Loader2Icon className="size-4 animate-spin" />
                                        ) : (
                                            <CheckIcon className="size-4" />
                                        )}
                                    </Button>
                                    <Button
                                        type="button"
                                        variant="ghost"
                                        size="icon-sm"
                                        onClick={cancelEditing}
                                        aria-label="Cancel rename"
                                    >
                                        <XIcon className="size-4" />
                                    </Button>
                                </div>
                            ) : (
                                <div className="flex items-center gap-2">
                                    <h1 className="text-base font-semibold tracking-[-0.01em]">
                                        {schedule.name}
                                    </h1>
                                    {schedule.isDefault && (
                                        <Badge variant="secondary">
                                            Default
                                        </Badge>
                                    )}
                                    <Button
                                        type="button"
                                        variant="ghost"
                                        size="icon-sm"
                                        className="text-muted-foreground"
                                        onClick={startEditing}
                                        aria-label="Rename schedule"
                                    >
                                        <PencilIcon className="size-4" />
                                    </Button>
                                </div>
                            )}
                        </div>
                    </div>
                </div>

                <Button type="button" onClick={handleSave} disabled={isSaving}>
                    {isSaving ? (
                        <Loader2Icon className="size-4 animate-spin" />
                    ) : (
                        "Save"
                    )}
                </Button>
            </div>

            {/* Days */}
            <div className="flex flex-col gap-2">
                {days.map((day) => (
                    <div
                        key={day.dayOfWeek}
                        className="flex items-center gap-4 rounded-xl bg-card px-4 py-3.5 ring-1 ring-foreground/10"
                    >
                        <Switch
                            checked={day.isAvailable}
                            onCheckedChange={(checked) =>
                                updateDay(day.dayOfWeek, {
                                    isAvailable: checked,
                                    startTime: checked
                                        ? (day.startTime ?? DEFAULT_DAY_START)
                                        : day.startTime,
                                    endTime: checked
                                        ? (day.endTime ?? DEFAULT_DAY_END)
                                        : day.endTime,
                                })
                            }
                        />

                        <span
                            className={
                                "w-24 shrink-0 text-sm font-medium " +
                                (!day.isAvailable
                                    ? "text-muted-foreground/60"
                                    : "")
                            }
                        >
                            {DAY_NAMES[day.dayOfWeek]}
                        </span>

                        {day.isAvailable ? (
                            <div className="flex items-center gap-2 flex-1">
                                <Input
                                    type="time"
                                    className="h-8 w-32"
                                    value={toTimeInput(day.startTime)}
                                    onChange={(e) =>
                                        updateDay(day.dayOfWeek, {
                                            startTime: e.target.value,
                                        })
                                    }
                                />
                                <span className="text-sm text-muted-foreground">
                                    –
                                </span>
                                <Input
                                    type="time"
                                    className="h-8 w-32"
                                    value={toTimeInput(day.endTime)}
                                    onChange={(e) =>
                                        updateDay(day.dayOfWeek, {
                                            endTime: e.target.value,
                                        })
                                    }
                                />
                            </div>
                        ) : (
                            <span className="flex-1 text-sm text-muted-foreground/60">
                                Unavailable
                            </span>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ScheduleDetailPage;
