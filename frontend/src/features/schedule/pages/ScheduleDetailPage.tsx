import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { ChevronLeft, Pencil, Check, X } from "lucide-react";
import toast from "react-hot-toast";
import axios from "axios";
import {
    getScheduleById,
    updateSchedule,
    updateScheduleName,
} from "../api/SchedulesApi";
import type { DailySchedule, Schedule } from "../types/Schedule";
import { useSchedulesContext } from "../context/schedulesContextStore";

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

const toTimeInput = (time: string) => time.slice(0, 5);

const ScheduleDetailPage = () => {
    const { id } = useParams() as { id: string };
    const navigate = useNavigate();
    const { updateLocal } = useSchedulesContext();

    const [schedule, setSchedule] = useState<Schedule | null>(null);
    const [days, setDays] = useState<DailySchedule[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isSaving, setIsSaving] = useState(false);

    // Name editing
    const [editingName, setEditingName] = useState(false);
    const [draftName, setDraftName] = useState("");
    const [isRenaming, setIsRenaming] = useState(false);
    const nameInputRef = useRef<HTMLInputElement>(null);

    useEffect(() => {
        getScheduleById(id)
            .then((s) => {
                setSchedule(s);
                setDays(
                    [...s.dailySchedules].sort(
                        (a, b) => a.dayOfWeek - b.dayOfWeek,
                    ),
                );
            })
            .catch((error) => {
                if (axios.isAxiosError(error)) {
                    toast.error(
                        error.response?.data?.message ??
                            "Failed to load schedule",
                    );
                } else {
                    toast.error("Something went wrong");
                }
            })
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
            const updated = await updateScheduleName(draftName.trim(), id);
            setSchedule(updated);
            updateLocal(updated);
            setEditingName(false);
            toast.success("Name updated");
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(
                    error.response?.data?.message ?? "Failed to rename",
                );
            } else {
                toast.error("Something went wrong");
            }
        } finally {
            setIsRenaming(false);
        }
    };

    const updateDay = (dayOfWeek: number, patch: Partial<DailySchedule>) => {
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
        setIsSaving(true);
        try {
            const updated = await updateSchedule(
                {
                    name: schedule.name,
                    isDefault: schedule.isDefault,
                    days: days.map((schedule) => ({
                        dayOfWeek: schedule.dayOfWeek,
                        startTime: schedule.startTime,
                        endTime: schedule.endTime,
                        isAvailable: schedule.isAvailable,
                    })),
                },
                id,
            );
            setSchedule(updated);
            updateLocal(updated);
            setDays(
                [...updated.dailySchedules].sort(
                    (a, b) => a.dayOfWeek - b.dayOfWeek,
                ),
            );
            toast.success("Schedule saved");
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(error.response?.data?.message);
            } else {
                toast.error("Something went wrong");
            }
        } finally {
            setIsSaving(false);
        }
    };

    if (isLoading)
        return (
            <div className="flex items-center justify-center h-64">
                <span className="loading loading-spinner loading-md text-primary" />
            </div>
        );

    if (!schedule)
        return (
            <div className="flex items-center justify-center h-64 text-base-content/40 text-sm">
                Schedule not found.
            </div>
        );

    return (
        <div className="p-6 max-w-3xl mx-auto">
            {/* Header */}
            <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-3">
                    <button
                        className="btn btn-ghost btn-sm btn-circle"
                        onClick={() => navigate(-1)}
                    >
                        <ChevronLeft className="w-4 h-4" />
                    </button>
                    <div>
                        <div className="flex items-center gap-2">
                            {editingName ? (
                                <div className="flex items-center gap-1.5">
                                    <input
                                        ref={nameInputRef}
                                        type="text"
                                        className="input input-bordered input-sm outline-none text-base font-bold w-48"
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
                                    <button
                                        type="button"
                                        className="btn btn-ghost btn-xs btn-square text-success"
                                        disabled={isRenaming}
                                        onClick={handleRenameSave}
                                    >
                                        {isRenaming ? (
                                            <span className="loading loading-spinner loading-xs" />
                                        ) : (
                                            <Check className="w-3.5 h-3.5" />
                                        )}
                                    </button>
                                    <button
                                        type="button"
                                        className="btn btn-ghost btn-xs btn-square"
                                        onClick={cancelEditing}
                                    >
                                        <X className="w-3.5 h-3.5" />
                                    </button>
                                </div>
                            ) : (
                                <div className="flex items-center gap-2">
                                    <h1 className="text-lg font-bold text-base-content">
                                        {schedule.name}
                                    </h1>
                                    {schedule.isDefault && (
                                        <span className="text-xs bg-primary/10 text-primary px-1.5 py-0.5 rounded-md font-medium">
                                            Default
                                        </span>
                                    )}
                                    <button
                                        type="button"
                                        className="btn btn-ghost btn-xs btn-square text-base-content/40 hover:text-base-content"
                                        onClick={startEditing}
                                    >
                                        <Pencil className="w-3.5 h-3.5" />
                                    </button>
                                </div>
                            )}
                        </div>
                    </div>
                </div>

                <button
                    className="btn btn-primary btn-sm"
                    onClick={handleSave}
                    disabled={isSaving}
                >
                    {isSaving ? (
                        <span className="loading loading-spinner loading-xs" />
                    ) : (
                        "Save"
                    )}
                </button>
            </div>

            {/* Days */}
            <div className="flex flex-col gap-2">
                {days.map((day) => (
                    <div
                        key={day.dayOfWeek}
                        className="flex items-center gap-4 px-4 py-3.5 border border-base-300 rounded-xl"
                    >
                        <input
                            type="checkbox"
                            className="toggle toggle-primary toggle-sm shrink-0"
                            checked={day.isAvailable}
                            onChange={(e) =>
                                updateDay(day.dayOfWeek, {
                                    isAvailable: e.target.checked,
                                })
                            }
                        />

                        <span
                            className={`text-sm font-medium w-24 shrink-0 ${!day.isAvailable ? "text-base-content/30" : ""}`}
                        >
                            {DAY_NAMES[day.dayOfWeek]}
                        </span>

                        {day.isAvailable ? (
                            <div className="flex items-center gap-2 flex-1">
                                <input
                                    type="time"
                                    className="input input-bordered input-sm outline-none w-32"
                                    value={toTimeInput(day.startTime)}
                                    onChange={(e) =>
                                        updateDay(day.dayOfWeek, {
                                            startTime: e.target.value,
                                        })
                                    }
                                />
                                <span className="text-base-content/40 text-sm">
                                    –
                                </span>
                                <input
                                    type="time"
                                    className="input input-bordered input-sm outline-none w-32"
                                    value={toTimeInput(day.endTime)}
                                    onChange={(e) =>
                                        updateDay(day.dayOfWeek, {
                                            endTime: e.target.value,
                                        })
                                    }
                                />
                            </div>
                        ) : (
                            <span className="text-sm text-base-content/30 flex-1">
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
