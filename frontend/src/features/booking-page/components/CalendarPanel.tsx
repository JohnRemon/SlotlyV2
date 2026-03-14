import { Button } from "@/components/ui/button";
import type { ScheduleResponse } from "@/features/schedule/types/Schedule";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { useCallback, useEffect, useMemo, useState } from "react";

interface CalendarPanelProps {
    selectedDate: string | null;
    onSelectDate: (date: string) => void;
    schedule?: ScheduleResponse;
}

const DAYS = ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"];
const MONTHS = [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December",
];

const dateGetDayToDayOfWeek = (jsDay: number): number =>
    jsDay === 0 ? 7 : jsDay;

const getToday = () => {
    const d = new Date();
    d.setHours(0, 0, 0, 0);
    return d;
};

export const CalendarPanel = ({
    selectedDate,
    onSelectDate,
    schedule,
}: CalendarPanelProps) => {
    const today = useMemo(() => getToday(), []);

    const [viewDate, setViewDate] = useState(
        () => new Date(today.getFullYear(), today.getMonth(), 1),
    );

    const year = viewDate.getFullYear();
    const month = viewDate.getMonth();
    const firstDay = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    const isScheduledDay = useCallback(
        (date: Date): boolean => {
            if (!schedule) return true;
            const dayOfWeek = dateGetDayToDayOfWeek(date.getDay());
            return (
                schedule.dailySchedules.find((d) => d.dayOfWeek === dayOfWeek)
                    ?.isAvailable ?? false
            );
        },
        [schedule],
    );

    const toDateStr = useCallback(
        (day: number): string => {
            const y = viewDate.getFullYear();
            const m = String(viewDate.getMonth() + 1).padStart(2, "0");
            const d = String(day).padStart(2, "0");
            return `${y}-${m}-${d}`;
        },
        [viewDate],
    );

    const isAvailable = useCallback(
        (day: number): boolean => {
            const d = new Date(
                viewDate.getFullYear(),
                viewDate.getMonth(),
                day,
            );
            d.setHours(0, 0, 0, 0);
            return d >= today && isScheduledDay(d);
        },
        [viewDate, today, isScheduledDay],
    );

    useEffect(() => {
        if (selectedDate) return;
        for (let day = 1; day <= daysInMonth; day++) {
            if (isAvailable(day)) {
                onSelectDate(toDateStr(day));
                break;
            }
        }
    }, [selectedDate, daysInMonth, isAvailable, toDateStr, onSelectDate]);

    const canGoPrev =
        new Date(year, month - 1, 1) >=
        new Date(today.getFullYear(), today.getMonth(), 1);

    return (
        <div className="flex h-full flex-col gap-4">
            <div className="flex items-center justify-between">
                <Button
                    type="button"
                    variant="ghost"
                    size="icon-sm"
                    onClick={() => setViewDate(new Date(year, month - 1, 1))}
                    disabled={!canGoPrev}
                    className="rounded-full"
                    aria-label="Previous month"
                >
                    <ChevronLeft className="size-4" />
                </Button>
                <span className="text-sm font-semibold tracking-[-0.01em]">
                    {MONTHS[month]} {year}
                </span>
                <Button
                    type="button"
                    variant="ghost"
                    size="icon-sm"
                    onClick={() => setViewDate(new Date(year, month + 1, 1))}
                    className="rounded-full"
                    aria-label="Next month"
                >
                    <ChevronRight className="size-4" />
                </Button>
            </div>

            <div className="grid grid-cols-7 text-center">
                {DAYS.map((d) => (
                    <span
                        key={d}
                        className="py-1 text-xs font-medium text-muted-foreground"
                    >
                        {d}
                    </span>
                ))}
            </div>

            <div className="grid grid-cols-7 gap-y-1 text-center">
                {Array.from({ length: firstDay }).map((_, i) => (
                    <span key={`empty-${i}`} />
                ))}
                {Array.from({ length: daysInMonth }).map((_, i) => {
                    const day = i + 1;
                    const dateStr = toDateStr(day);
                    const avail = isAvailable(day);
                    const selected = selectedDate === dateStr;

                    return (
                        <button
                            key={day}
                            type="button"
                            disabled={!avail}
                            onClick={() => avail && onSelectDate(dateStr)}
                            className={[
                                "mx-auto flex size-9 items-center justify-center rounded-md text-sm font-medium",
                                "focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-ring/50",
                                selected
                                    ? "bg-primary text-primary-foreground"
                                    : avail
                                      ? "text-foreground hover:bg-muted/50 transition-colors"
                                      : "cursor-not-allowed text-muted-foreground/30 pointer-events-none",
                            ].join(" ")}
                        >
                            {day}
                        </button>
                    );
                })}
            </div>
        </div>
    );
};
