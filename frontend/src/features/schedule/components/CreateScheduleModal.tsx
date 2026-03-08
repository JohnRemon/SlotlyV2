import axios from "axios";
import { X } from "lucide-react";
import { useState } from "react";
import toast from "react-hot-toast";
import { useSchedules } from "../hooks/useSchedules";

interface Props {
    onClose: () => void;
    onSuccess: (id: string) => void;
}

const CreateScheduleModal = ({ onClose, onSuccess }: Props) => {
    const { create } = useSchedules();
    const [name, setName] = useState("");
    const [isCreating, setIsCreating] = useState(false);

    const handleCreate = async () => {
        setIsCreating(true);
        try {
            const newSchedule = await create({
                name: name.trim(),
            });
            toast.success("Schedule created");
            onSuccess(newSchedule.id);
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(
                    error.response?.data?.message ?? "Failed to create",
                );
            } else {
                toast.error("Something went wrong");
            }
        } finally {
            setIsCreating(false);
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
            <div className="bg-base-100 rounded-2xl p-6 w-full max-w-sm shadow-xl flex flex-col gap-5">
                <div className="flex items-center justify-between">
                    <h2 className="text-sm font-semibold">New schedule</h2>
                    <button
                        type="button"
                        className="btn btn-ghost btn-xs btn-square"
                        onClick={onClose}
                    >
                        <X className="w-4 h-4" />
                    </button>
                </div>

                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium">Name</label>
                    <input
                        type="text"
                        className="input input-bordered w-full outline-none"
                        placeholder="e.g. Work hours"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && handleCreate()}
                        autoFocus
                    />
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
                        disabled={!name.trim() || isCreating}
                        onClick={handleCreate}
                    >
                        {isCreating ? (
                            <span className="loading loading-spinner loading-xs" />
                        ) : (
                            "Create"
                        )}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default CreateScheduleModal;
