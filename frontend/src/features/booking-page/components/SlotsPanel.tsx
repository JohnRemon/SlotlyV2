import type { Slot } from "@/features/slots/types/Slots";

interface SlotsPanelProps {
    slots: Slot[];
    isLoading: boolean;
    selectedSlotId: number | null;
    onSelectSlot: (slot: Slot) => void;
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
                <span className="loading loading-spinner loading-sm text-primary" />
            </div>
        );

    if (slots.length === 0)
        return (
            <div className="flex-1 flex items-center justify-center text-center">
                <p className="text-sm text-base-content/40">
                    No available slots for this day
                </p>
            </div>
        );

    return (
        <div className="flex flex-col gap-2 h-64 scrollbar-hide overflow-y-auto pr-1 pl-1">
            {slots.map((slot) => {
                const isSelected = selectedSlotId === slot.id;
                return (
                    <button
                        key={slot.id}
                        onClick={() => onSelectSlot(slot)}
                        className={`w-full px-4 py-2.5 rounded-lg border text-sm font-medium transition-all duration-150 text-center cursor-pointer
                            ${
                                isSelected
                                    ? "bg-base-content text-base-100 border-base-content"
                                    : "bg-base-100 border-base-300 text-base-content hover:border-base-content/50 hover:bg-base-200 hover:scale-[1.01] active:scale-[0.99]"
                            }`}
                    >
                        {formatTime(slot.startTime)}
                    </button>
                );
            })}
        </div>
    );
};
