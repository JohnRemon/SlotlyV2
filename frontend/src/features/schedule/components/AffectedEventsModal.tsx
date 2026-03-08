import { useState } from "react";
import type { Schedule } from "../types/Schedule";
import type { Event } from "../../events/types/Event";

const AffectedEventsModal = ({
    events,
    schedules,
    currentScheduleId,
    onConfirm,
    onClose,
}: {
    events: Event[];
    schedules: Schedule[];
    currentScheduleId: string;
    onConfirm: (assignments: Record<string, string>) => void;
    onClose: () => void;
}) => {
    // default all events to first available schedule that isn't the one being deleted
    const fallback = schedules.find((s) => s.id !== currentScheduleId);
    const [assignments, setAssignments] = useState<Record<string, string>>(
        Object.fromEntries(events.map((e) => [e.id, fallback?.id ?? ""])),
    );

    const availableSchedules = schedules.filter(
        (s) => s.id !== currentScheduleId,
    );

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
            <div className="bg-base-100 rounded-2xl p-6 w-full max-w-md shadow-xl flex flex-col gap-5">
                <div>
                    <h2 className="text-sm font-semibold">
                        Reassign affected events
                    </h2>
                    <p className="text-xs text-base-content/40 mt-0.5">
                        {events.length} event{events.length !== 1 ? "s" : ""}{" "}
                        used this schedule. Choose a replacement for each.
                    </p>
                </div>

                {events.length === 0 ? (
                    <p className="text-sm text-base-content/40 text-center py-4">
                        No events were using this schedule.
                    </p>
                ) : (
                    <div className="flex flex-col gap-3 max-h-72 overflow-y-auto">
                        {events.map((event) => (
                            <div
                                key={event.id}
                                className="flex items-center justify-between gap-3"
                            >
                                <span className="text-sm font-medium truncate flex-1">
                                    {event.eventName}
                                </span>
                                <select
                                    className="select select-bordered select-sm outline-none w-40 shrink-0"
                                    value={assignments[event.id] ?? ""}
                                    onChange={(e) =>
                                        setAssignments((prev) => ({
                                            ...prev,
                                            [event.id]: e.target.value,
                                        }))
                                    }
                                >
                                    {availableSchedules.map((s) => (
                                        <option key={s.id} value={s.id}>
                                            {s.name}
                                            {s.isDefault ? " (Default)" : ""}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        ))}
                    </div>
                )}

                <div className="flex gap-2">
                    <button
                        type="button"
                        className="btn btn-outline btn-sm flex-1"
                        onClick={onClose}
                    >
                        Cancel
                    </button>
                    <button
                        type="button"
                        className="btn btn-error btn-sm flex-1"
                        onClick={() => onConfirm(assignments)}
                    >
                        Delete & reassign
                    </button>
                </div>
            </div>
        </div>
    );
};

export default AffectedEventsModal;
