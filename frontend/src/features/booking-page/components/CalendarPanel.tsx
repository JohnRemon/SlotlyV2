import { ChevronLeft, ChevronRight } from "lucide-react";
import { useState } from "react";

import { Button } from "@/components/ui/button";

interface CalendarPanelProps {
    eventStart: string;
    eventEnd: string;
    selectedDate: string | null;
    onSelectDate: (date: string) => void;
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

export const CalendarPanel = ({
    eventStart,
    eventEnd,
    selectedDate,
    onSelectDate,
}: CalendarPanelProps) => {
    const start = new Date(eventStart);
    const end = new Date(eventEnd);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const [viewDate, setViewDate] = useState(() => {
        const d = start > today ? new Date(start) : new Date(today);
        return new Date(d.getFullYear(), d.getMonth(), 1);
    });

    const year = viewDate.getFullYear();
    const month = viewDate.getMonth();

    const firstDay = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    const isAvailable = (day: number) => {
        const d = new Date(year, month, day);
        d.setHours(0, 0, 0, 0);
        const s = new Date(start);
        s.setHours(0, 0, 0, 0);
        const e = new Date(end);
        e.setHours(0, 0, 0, 0);
        return d >= s && d <= e && d >= today;
    };

    const toDateStr = (day: number) => {
        const month2 = String(month + 1).padStart(2, "0");
        const day2 = String(day).padStart(2, "0");
        return `${year}-${month2}-${day2}`;
    };

    const prevMonth = () => setViewDate(new Date(year, month - 1, 1));
    const nextMonth = () => setViewDate(new Date(year, month + 1, 1));

    const canGoPrev =
        new Date(year, month - 1, 1) >=
        new Date(start.getFullYear(), start.getMonth(), 1);
    const canGoNext =
        new Date(year, month + 1, 1) <=
        new Date(end.getFullYear(), end.getMonth(), 1);

    return (
        <div className="flex h-full flex-col gap-4">
            {/* Month Nav */}
            <div className="flex items-center justify-between">
                <Button
                    type="button"
                    variant="ghost"
                    size="icon-sm"
                    onClick={prevMonth}
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
                    onClick={nextMonth}
                    disabled={!canGoNext}
                    className="rounded-full"
                    aria-label="Next month"
                >
                    <ChevronRight className="size-4" />
                </Button>
            </div>

            {/* Day Headers */}
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

            {/* Days */}
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
                            disabled={!avail}
                            onClick={() => onSelectDate(dateStr)}
                            className={
                                "mx-auto flex size-9 items-center justify-center rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-ring/50 " +
                                (selected
                                    ? "bg-primary text-primary-foreground"
                                    : avail
                                      ? "text-foreground hover:bg-muted/50"
                                      : "cursor-not-allowed text-muted-foreground/30")
                            }
                        >
                            {day}
                        </button>
                    );
                })}
            </div>
        </div>
    );
};
