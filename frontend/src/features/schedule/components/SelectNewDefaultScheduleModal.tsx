import { Check } from "lucide-react";
import { useState } from "react";
import type { Schedule } from "../types/Schedule";

interface SelectNewDefaultModalProps {
    schedules: Schedule[];
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
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
            <div className="bg-base-100 rounded-2xl p-6 w-full max-w-sm shadow-xl flex flex-col gap-5">
                <div>
                    <h2 className="text-sm font-semibold">Set a new default</h2>
                    <p className="text-xs text-base-content/40 mt-0.5">
                        Choose a schedule to become the new default before
                        deleting this one
                    </p>
                </div>

                <div className="flex flex-col gap-2">
                    {schedules.map((s) => (
                        <button
                            key={s.id}
                            type="button"
                            onClick={() => setSelected(s.id)}
                            className={`flex items-center justify-between px-3 py-2.5 border rounded-xl text-sm transition-colors text-left
                                ${
                                    selected === s.id
                                        ? "border-primary bg-primary/5"
                                        : "border-base-300 hover:border-base-content/30"
                                }`}
                        >
                            <span className="font-medium">{s.name}</span>
                            {selected === s.id && (
                                <Check className="w-3.5 h-3.5 text-primary" />
                            )}
                        </button>
                    ))}
                </div>

                <div className="flex gap-2">
                    <button
                        type="button"
                        className="btn btn-outline btn-sm flex-1"
                        onClick={onClose}
                    >
                        Cancel
                    </button>
                    <button
                        type="button"
                        className="btn btn-primary btn-sm flex-1"
                        disabled={!selected}
                        onClick={() => selected && onConfirm(selected)}
                    >
                        Continue
                    </button>
                </div>
            </div>
        </div>
    );
};

export default SelectNewDefaultModal;
