import { useState } from "react";
import type { ScheduleResponse } from "../types/Schedule";
import type { EventResponse } from "../../events/types/Event";

import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogTitle } from "@/components/ui/dialog";
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";

const AffectedEventsModal = ({
    events,
    schedules,
    currentScheduleId,
    onConfirm,
    onClose,
}: {
    events: EventResponse[];
    schedules: ScheduleResponse[];
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
        <Dialog open>
            <DialogContent showCloseButton={false} className="sm:max-w-md">
                <div>
                    <DialogTitle>Reassign affected events</DialogTitle>
                    <p className="mt-1 text-xs text-muted-foreground">
                        {events.length} event{events.length !== 1 ? "s" : ""}{" "}
                        used this schedule. Choose a replacement for each.
                    </p>
                </div>

                {events.length === 0 ? (
                    <p className="py-4 text-center text-sm text-muted-foreground">
                        No events were using this schedule.
                    </p>
                ) : (
                    <div className="flex max-h-72 flex-col gap-3 overflow-y-auto pr-1">
                        {events.map((event) => (
                            <div
                                key={event.id}
                                className="flex items-center justify-between gap-3"
                            >
                                <span className="min-w-0 flex-1 truncate text-sm font-medium">
                                    {event.eventName}
                                </span>
                                <div className="shrink-0">
                                    <Select
                                        value={assignments[event.id] ?? ""}
                                        onValueChange={(value) =>
                                            setAssignments((prev) => ({
                                                ...prev,
                                                [event.id]: value,
                                            }))
                                        }
                                    >
                                        <SelectTrigger
                                            size="sm"
                                            className="w-44"
                                        >
                                            <SelectValue placeholder="Select schedule" />
                                        </SelectTrigger>
                                        <SelectContent align="end">
                                            <SelectGroup>
                                                {availableSchedules.map((s) => (
                                                    <SelectItem
                                                        key={s.id}
                                                        value={s.id}
                                                    >
                                                        {s.name}
                                                        {s.isDefault
                                                            ? " (Default)"
                                                            : ""}
                                                    </SelectItem>
                                                ))}
                                            </SelectGroup>
                                        </SelectContent>
                                    </Select>
                                </div>
                            </div>
                        ))}
                    </div>
                )}

                <div className="flex gap-2 pt-1">
                    <Button
                        type="button"
                        variant="outline"
                        className="flex-1"
                        onClick={onClose}
                    >
                        Cancel
                    </Button>
                    <Button
                        type="button"
                        variant="destructive"
                        className="flex-1"
                        onClick={() => onConfirm(assignments)}
                    >
                        Delete & reassign
                    </Button>
                </div>
            </DialogContent>
        </Dialog>
    );
};

export default AffectedEventsModal;
