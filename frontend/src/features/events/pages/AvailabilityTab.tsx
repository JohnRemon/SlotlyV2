import { useState } from "react";
import { Link } from "react-router";
import { ExternalLink } from "lucide-react";
import type { ScheduleResponse } from "../../schedule/types/Schedule";

import FormField from "@/components/common/FormField";
import LoadingSpinner from "@/components/common/LoadingSpinner";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";

interface Props {
    scheduleId: string;
    scheduleIsDefault: boolean;
    schedules: ScheduleResponse[];
    schedulesLoading: boolean;
    onScheduleChange: (scheduleId: string) => Promise<void>;
}

const DAY_NAMES = ["", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

const formatTime = (time?: string | null) => {
    if (!time) return "--";

    // time is "HH:mm:ss" or "HH:mm"
    const [h, m] = time.split(":");
    const hour = parseInt(h);
    const ampm = hour >= 12 ? "PM" : "AM";
    const display = hour % 12 === 0 ? 12 : hour % 12;
    return `${display}:${m} ${ampm}`;
};

export const AvailabilityTab = ({
    scheduleId,
    scheduleIsDefault,
    schedules,
    schedulesLoading,
    onScheduleChange,
}: Props) => {
    const [isSwitching, setIsSwitching] = useState(false);

    const currentSchedule = schedules.find((s) => s.id === scheduleId);

    const handleSelect = async (id: string) => {
        if (id === scheduleId) return;
        setIsSwitching(true);
        try {
            await onScheduleChange(id);
        } finally {
            setIsSwitching(false);
        }
    };

    return (
        <div className="flex flex-col gap-5">
            {isSwitching && (
                <div className="flex justify-end">
                    <LoadingSpinner label="Switching schedule" size="sm" />
                </div>
            )}

            <FormField
                label="Schedule"
                hint="Slots are generated based on this schedule's working hours."
            >
                <Select
                    value={currentSchedule?.id}
                    onValueChange={(value) => {
                        if (value) {
                            void handleSelect(value);
                        }
                    }}
                >
                    <SelectTrigger
                        className="w-full"
                        disabled={schedulesLoading || isSwitching}
                    >
                        <SelectValue
                            placeholder={
                                schedulesLoading
                                    ? "Loading schedules..."
                                    : "Select a schedule"
                            }
                        />
                        {scheduleIsDefault && (
                            <Badge variant="secondary">Default</Badge>
                        )}
                    </SelectTrigger>
                    <SelectContent align="start">
                        {schedules.map((s) => (
                            <SelectItem key={s.id} value={s.id}>
                                {s.name}
                                {s.isDefault && (
                                    <Badge variant="outline">Default</Badge>
                                )}
                            </SelectItem>
                        ))}
                    </SelectContent>
                </Select>
            </FormField>

            {currentSchedule && (
                <Link
                    to={`/schedules/${currentSchedule.id}`}
                    className="inline-flex w-fit items-center gap-1 text-xs text-primary hover:underline"
                >
                    <ExternalLink className="size-3" />
                    Edit this schedule
                </Link>
            )}

            {currentSchedule && (
                <Card size="sm" className="gap-0 overflow-hidden">
                    <CardContent className="px-0">
                        <div className="divide-y divide-border">
                            {[...currentSchedule.dailySchedules]
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
                                            {day.isAvailable ? (
                                                <span className="text-sm text-muted-foreground">
                                                    {formatTime(day.startTime)}{" "}
                                                    – {formatTime(day.endTime)}
                                                </span>
                                            ) : (
                                                <span className="text-sm text-muted-foreground">
                                                    Unavailable
                                                </span>
                                            )}
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
                    </CardContent>
                </Card>
            )}
        </div>
    );
};
