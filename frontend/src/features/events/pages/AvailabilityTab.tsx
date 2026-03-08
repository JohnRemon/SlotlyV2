import { useState } from "react";
import { Link } from "react-router";
import { ExternalLink, ChevronDown, Check } from "lucide-react";
import type { Schedule } from "../../schedule/types/Schedule";

interface Props {
    scheduleId: string;
    scheduleIsDefault: boolean;
    schedules: Schedule[];
    schedulesLoading: boolean;
    onScheduleChange: (scheduleId: string) => Promise<void>;
}

const DAY_NAMES = ["", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

const formatTime = (time: string) => {
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
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const [isSwitching, setIsSwitching] = useState(false);

    const currentSchedule = schedules.find((s) => s.id === scheduleId);

    const handleSelect = async (id: string) => {
        if (id === scheduleId) {
            setDropdownOpen(false);
            return;
        }
        setIsSwitching(true);
        setDropdownOpen(false);
        try {
            await onScheduleChange(id);
        } finally {
            setIsSwitching(false);
        }
    };

    return (
        <div className="flex flex-col gap-6">
            <h2 className="text-sm font-semibold text-base-content">
                Availability
            </h2>

            {/* Schedule selector */}
            <div className="flex flex-col gap-2">
                <label className="text-sm font-medium">Schedule</label>
                <p className="text-xs text-base-content/40">
                    Slots are generated based on this schedule's working hours.
                </p>

                <div className="relative">
                    <button
                        type="button"
                        disabled={schedulesLoading || isSwitching}
                        onClick={() => setDropdownOpen((v) => !v)}
                        className="flex items-center justify-between w-full px-3 py-2.5 border border-base-300 rounded-xl text-sm hover:border-base-content/30 transition-colors disabled:opacity-50"
                    >
                        <div className="flex items-center gap-2">
                            {isSwitching ? (
                                <span className="loading loading-spinner loading-xs" />
                            ) : null}
                            <span className="font-medium">
                                {schedulesLoading
                                    ? "Loading..."
                                    : currentSchedule?.name}
                            </span>
                            {scheduleIsDefault && (
                                <span className="text-xs bg-primary/10 text-primary px-1.5 py-0.5 rounded-md font-medium">
                                    Default
                                </span>
                            )}
                        </div>
                        <ChevronDown
                            className={`w-4 h-4 text-base-content/40 transition-transform ${dropdownOpen ? "rotate-180" : ""}`}
                        />
                    </button>

                    {dropdownOpen && (
                        <div className="absolute top-full mt-1 left-0 right-0 z-10 bg-base-100 border border-base-300 rounded-xl shadow-lg overflow-hidden">
                            {schedules.map((s) => (
                                <button
                                    key={s.id}
                                    type="button"
                                    onClick={() => handleSelect(s.id)}
                                    className="flex items-center justify-between w-full px-3 py-2.5 text-sm hover:bg-base-200 transition-colors text-left"
                                >
                                    <div className="flex items-center gap-2">
                                        <span>{s.name}</span>
                                        {s.isDefault && (
                                            <span className="text-xs bg-primary/10 text-primary px-1.5 py-0.5 rounded-md font-medium">
                                                Default
                                            </span>
                                        )}
                                    </div>
                                    {s.id === scheduleId && (
                                        <Check className="w-3.5 h-3.5 text-primary" />
                                    )}
                                </button>
                            ))}
                        </div>
                    )}
                </div>

                {currentSchedule && (
                    <Link
                        to={`/schedules/${currentSchedule.id}`}
                        className="flex items-center gap-1 text-xs text-primary hover:underline w-fit mt-0.5"
                    >
                        <ExternalLink className="w-3 h-3" />
                        Edit this schedule
                    </Link>
                )}
            </div>

            {/* Schedule detail view — read only */}
            {currentSchedule && (
                <div className="flex flex-col gap-1 border border-base-300 rounded-xl overflow-hidden">
                    {[...currentSchedule.dailySchedules]
                        .sort((a, b) => a.dayOfWeek - b.dayOfWeek)
                        .map((day) => (
                            <div
                                key={day.dayOfWeek}
                                className="flex items-center justify-between px-4 py-3 border-b border-base-300 last:border-b-0"
                            >
                                <div className="flex items-center gap-3">
                                    <span
                                        className={`text-sm font-medium w-8 ${!day.isAvailable ? "text-base-content/30" : ""}`}
                                    >
                                        {DAY_NAMES[day.dayOfWeek]}
                                    </span>
                                    {day.isAvailable ? (
                                        <span className="text-sm text-base-content/70">
                                            {formatTime(day.startTime)} –{" "}
                                            {formatTime(day.endTime)}
                                        </span>
                                    ) : (
                                        <span className="text-sm text-base-content/30">
                                            Unavailable
                                        </span>
                                    )}
                                </div>
                                <div
                                    className={`w-2 h-2 rounded-full ${day.isAvailable ? "bg-success" : "bg-base-300"}`}
                                />
                            </div>
                        ))}
                </div>
            )}
        </div>
    );
};
