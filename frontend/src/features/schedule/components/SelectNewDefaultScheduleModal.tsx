import { CheckIcon } from "lucide-react";
import { useState } from "react";
import type { ScheduleResponse } from "../types/Schedule";

import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogTitle } from "@/components/ui/dialog";

interface SelectNewDefaultModalProps {
    schedules: ScheduleResponse[];
    onConfirm: (id: string) => void;
    onClose: () => void;
}
const SelectNewDefaultModal = ({
    schedules,
    onConfirm,
    onClose,
}: SelectNewDefaultModalProps) => {
    const [selected, setSelected] = useState<string | null>(null);

    return (
        <Dialog open>
            <DialogContent showCloseButton={false} className="sm:max-w-sm">
                <div>
                    <DialogTitle>Set a new default</DialogTitle>
                    <p className="mt-1 text-xs text-muted-foreground">
                        Choose a schedule to become the new default before
                        deleting this one.
                    </p>
                </div>

                <div className="flex flex-col gap-2">
                    {schedules.map((s) => {
                        const isSelected = selected === s.id;
                        return (
                            <button
                                key={s.id}
                                type="button"
                                onClick={() => setSelected(s.id)}
                                className={
                                    "flex items-center justify-between rounded-xl px-3 py-2.5 text-left text-sm transition-colors ring-1 ring-border hover:bg-muted/30 focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-ring/50 " +
                                    (isSelected
                                        ? "bg-primary/10 ring-primary/25"
                                        : "bg-card")
                                }
                            >
                                <span className="font-medium">{s.name}</span>
                                {isSelected && (
                                    <CheckIcon className="size-4 text-primary" />
                                )}
                            </button>
                        );
                    })}
                </div>

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
                        className="flex-1"
                        disabled={!selected}
                        onClick={() => selected && onConfirm(selected)}
                    >
                        Continue
                    </Button>
                </div>
            </DialogContent>
        </Dialog>
    );
};

export default SelectNewDefaultModal;
