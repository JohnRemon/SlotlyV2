import { ExternalLink, Loader2Icon } from "lucide-react";
import { useState } from "react";
import { Link } from "react-router";
import type { ScheduleResponse } from "../../schedule/types/Schedule";

import FormField from "@/components/common/FormField";
import { Badge } from "@/components/ui/badge";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
} from "@/components/ui/select";

interface Props {
    schedule: ScheduleResponse;
    schedules: ScheduleResponse[];
    schedulesLoading: boolean;
    onScheduleChange: (scheduleId: string) => Promise<void>;
}

const DAY_NAMES = ["", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

const formatTime = (time?: string | null) => {
    if (!time) return "--";
    const [h, m] = time.split(":");
    const hour = parseInt(h);
    const ampm = hour >= 12 ? "PM" : "AM";
    const display = hour % 12 === 0 ? 12 : hour % 12;
    return `${display}:${m} ${ampm}`;
};

export const AvailabilityTab = ({
    schedule,
    schedules,
    schedulesLoading,
    onScheduleChange,
}: Props) => {
    const [isSwitching, setIsSwitching] = useState(false);

    const handleSelect = async (id: string) => {
        if (id === schedule.id) return;
        setIsSwitching(true);
        try {
            await onScheduleChange(id);
        } finally {
            setIsSwitching(false);
        }
    };

    return (
        <div className="flex flex-col gap-5">
            <div className="flex items-end gap-3">
                <FormField
                    label="Schedule"
                    hint="Slots are generated based on this schedule's working hours."
                    className="flex-1"
                >
                    <Select
                        value={schedule.id}
                        onValueChange={(value) => {
                            if (value) void handleSelect(value);
                        }}
                    >
                        <SelectTrigger
                            className="w-full"
                            disabled={schedulesLoading || isSwitching}
                        >
                            <div className="flex min-w-0 items-center gap-2">
                                <span className="truncate">
                                    {schedule.name}
                                </span>
                                {schedule.isDefault && (
                                    <Badge variant="secondary">Default</Badge>
                                )}
                            </div>
                        </SelectTrigger>
                        <SelectContent align="start">
                            {schedules.map((s) => (
                                <SelectItem key={s.id} value={s.id}>
                                    <span className="flex items-center gap-2">
                                        {s.name}
                                        {s.isDefault && (
                                            <Badge variant="outline">
                                                Default
                                            </Badge>
                                        )}
                                    </span>
                                </SelectItem>
                            ))}
                        </SelectContent>
                    </Select>
                </FormField>

                {isSwitching && (
                    <div className="mb-[9px] shrink-0">
                        <Loader2Icon className="size-4 animate-spin text-muted-foreground" />
                    </div>
                )}
            </div>

            <Link
                to={`/schedules/${schedule.id}`}
                className="inline-flex w-fit items-center gap-1 text-xs text-primary hover:underline"
            >
                <ExternalLink className="size-3" />
                Edit this schedule
            </Link>

            <div className="overflow-hidden rounded-2xl border ring-1 ring-foreground/5">
                <div className="border-b bg-muted/20 px-4 py-2.5">
                    <p className="text-xs font-medium text-muted-foreground">
                        Weekly hours
                    </p>
                </div>
                <div className="divide-y divide-border">
                    {[...schedule.dailySchedules]
                        .sort((a, b) => a.dayOfWeek - b.dayOfWeek)
                        .map((day) => (
                            <div
                                key={day.dayOfWeek}
                                className="flex items-center justify-between gap-4 px-4 py-3"
                            >
                                <div className="flex min-w-0 items-center gap-3">
                                    <span
                                        className={
                                            day.isAvailable
                                                ? "w-8 text-sm font-medium"
                                                : "w-8 text-sm font-medium text-muted-foreground"
                                        }
                                    >
                                        {DAY_NAMES[day.dayOfWeek]}
                                    </span>
                                    <span className="text-sm text-muted-foreground">
                                        {day.isAvailable
                                            ? `${formatTime(day.startTime)} – ${formatTime(day.endTime)}`
                                            : "Unavailable"}
                                    </span>
                                </div>
                                <span
                                    aria-hidden="true"
                                    className={
                                        day.isAvailable
                                            ? "size-2 rounded-full bg-primary"
                                            : "size-2 rounded-full bg-muted"
                                    }
                                />
                            </div>
                        ))}
                </div>
            </div>
        </div>
    );
};
