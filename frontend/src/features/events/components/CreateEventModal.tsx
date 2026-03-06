import axios from "axios";
import { X } from "lucide-react";
import { useState } from "react";
import toast from "react-hot-toast";
import type { EventRequest } from "../types/Event";

interface CreateEventModalProps {
    onClose: () => void;
    onCreate: (payload: EventRequest) => Promise<void>;
}

const CreateEventModal = ({ onClose, onCreate }: CreateEventModalProps) => {
    const [eventName, setEventName] = useState("");
    const [description, setDescription] = useState("");
    const [eventStart, setEventStart] = useState("");
    const [eventEnd, setEventEnd] = useState("");
    const [slotDuration, setSlotDuration] = useState(30);
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        setIsLoading(true);

        try {
            await onCreate({
                eventName,
                description,
                eventStart: new Date(eventStart).toISOString(),
                eventEnd: new Date(eventEnd).toISOString(),
                availabilityRulesDTO: {
                    slotDurationMinutes: slotDuration,
                },
            });
            toast.success("Event created!");
            onClose();
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(error.response?.data?.message);
            } else {
                toast.error("Something went wrong.");
            }
        } finally {
            setIsLoading(false);
        }
    };
    return (
        <dialog className="modal modal-open">
            <div className="modal-box rounded-2xl max-w-md p-0 overflow-hidden">
                {/* Header */}
                <div className="flex items-center justify-between px-6 py-4 border-b border-base-300">
                    <h3 className="font-bold text-base">New Event</h3>
                    <button
                        className="btn btn-ghost btn-xs btn-circle"
                        onClick={onClose}
                    >
                        <X className="w-4 h-4" />
                    </button>
                </div>

                {/* Form */}
                <form
                    onSubmit={handleSubmit}
                    className="px-6 py-5 flex flex-col gap-4"
                >
                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium">
                            Event name
                        </label>
                        <input
                            type="text"
                            className="input input-bordered w-full"
                            placeholder="e.g. 30 min meeting"
                            value={eventName}
                            onChange={(e) => setEventName(e.target.value)}
                            required
                        />
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium">
                            Description{" "}
                            <span className="text-base-content/40">
                                (optional)
                            </span>
                        </label>
                        <textarea
                            className="textarea textarea-bordered w-full resize-none"
                            rows={2}
                            placeholder="What is this event about?"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                        />
                    </div>

                    <div className="flex gap-3">
                        <div className="flex flex-col gap-1.5 flex-1">
                            <label className="text-sm font-medium">
                                Start date
                            </label>
                            <input
                                type="datetime-local"
                                className="input input-bordered w-full"
                                value={eventStart}
                                onChange={(e) => setEventStart(e.target.value)}
                                required
                            />
                        </div>
                        <div className="flex flex-col gap-1.5 flex-1">
                            <label className="text-sm font-medium">
                                End date
                            </label>
                            <input
                                type="datetime-local"
                                className="input input-bordered w-full"
                                value={eventEnd}
                                onChange={(e) => setEventEnd(e.target.value)}
                                required
                            />
                        </div>
                    </div>

                    <div className="flex gap-3 items-end">
                        <div className="flex flex-col gap-1.5 flex-1">
                            <label className="text-sm font-medium">
                                Slot duration (min)
                            </label>
                            <input
                                type="number"
                                className="input input-bordered w-full"
                                min={5}
                                step={5}
                                value={slotDuration}
                                onChange={(e) =>
                                    setSlotDuration(Number(e.target.value))
                                }
                                required
                            />
                        </div>
                    </div>

                    {/* Footer */}
                    <div className="flex gap-2 pt-1">
                        <button
                            type="button"
                            className="btn btn-outline btn-sm flex-1"
                            onClick={onClose}
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="btn btn-primary btn-sm flex-1"
                            disabled={isLoading}
                        >
                            {isLoading ? (
                                <span className="loading loading-spinner loading-xs" />
                            ) : (
                                "Create event"
                            )}
                        </button>
                    </div>
                </form>
            </div>
            <div className="modal-backdrop" onClick={onClose} />
        </dialog>
    );
};

export default CreateEventModal;
