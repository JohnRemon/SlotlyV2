import type { SlotResponse } from "@/features/slots/types/Slots";
import { Loader2Icon } from "lucide-react";

interface SlotsPanelProps {
    slots: SlotResponse[];
    isLoading: boolean;
    selectedSlotId: number | null;
    onSelectSlot: (slot: SlotResponse) => void;
}

const formatTime = (iso: string) =>
    new Date(iso).toLocaleTimeString("en-US", {
        hour: "2-digit",
        minute: "2-digit",
    });

export const SlotsPanel = ({
    slots,
    isLoading,
    selectedSlotId,
    onSelectSlot,
}: SlotsPanelProps) => {
    if (isLoading)
        return (
            <div className="h-full flex items-center justify-center">
                <Loader2Icon className="size-4 animate-spin text-muted-foreground" />
            </div>
        );

    if (slots.length === 0)
        return (
            <div className="flex-1 flex items-center justify-center text-center">
                <p className="text-sm text-muted-foreground">
                    No available slots for this day
                </p>
            </div>
        );

    return (
        <div className="flex h-64 flex-col gap-2 overflow-y-auto px-1 py-1 scrollbar-hide">
            {slots.map((slot) => {
                const isSelected = selectedSlotId === slot.id;
                return (
                    <button
                        key={slot.id}
                        type="button"
                        onClick={() => onSelectSlot(slot)}
                        className={
                            "w-full rounded-xl px-4 py-2.5 text-center text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-ring/50 " +
                            (isSelected
                                ? "bg-primary text-primary-foreground ring-1 ring-primary/25"
                                : "bg-background/20 text-foreground ring-1 ring-border hover:bg-muted/40")
                        }
                    >
                        {formatTime(slot.startTime)}
                    </button>
                );
            })}
        </div>
    );
};
