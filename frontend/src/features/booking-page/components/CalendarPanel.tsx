import { ChevronLeft, ChevronRight } from "lucide-react";
import { useState } from "react";

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
        <div className="flex flex-col gap-4 h-full ">
            {/* Month Nav */}
            <div className="flex items-center justify-between">
                <button
                    className="btn btn-ghost btn-xs btn-circle"
                    onClick={prevMonth}
                    disabled={!canGoPrev}
                >
                    <ChevronLeft className="w-4 h-4" />
                </button>
                <span className="text-sm font-semibold">
                    {MONTHS[month]} {year}
                </span>
                <button
                    className="btn btn-ghost btn-xs btn-circle"
                    onClick={nextMonth}
                    disabled={!canGoNext}
                >
                    <ChevronRight className="w-4 h-4" />
                </button>
            </div>

            {/* Day Headers */}
            <div className="grid grid-cols-7 text-center">
                {DAYS.map((d) => (
                    <span
                        key={d}
                        className="text-xs text-base-content/40 font-medium py-1"
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
                            className={`mx-auto w-9 h-9 rounded-sm text-sm font-medium transition-colors flex items-center justify-center
                                ${
                                    selected
                                        ? "bg-primary text-primary-content"
                                        : avail
                                          ? "hover:bg-base-200 text-base-content cursor-pointer"
                                          : "text-base-content/20 cursor-not-allowed"
                                }`}
                        >
                            {day}
                        </button>
                    );
                })}
            </div>
        </div>
    );
};
