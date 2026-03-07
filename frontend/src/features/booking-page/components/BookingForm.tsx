import { useState } from "react";
import toast from "react-hot-toast";
import { createBooking } from "../../bookings/api/BookingsApi";
import type { PublicEvent, Slot, FormField } from "../types/BookingSlots";
import axios from "axios";

interface BookingFormProps {
    event: PublicEvent;
    slot: Slot;
    onSuccess: () => void;
    onBack: () => void;
}

const formatDateTime = (iso: string) =>
    new Date(iso).toLocaleString("en-US", {
        weekday: "short",
        month: "short",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    });

export const BookingForm = ({
    event,
    slot,
    onSuccess,
    onBack,
}: BookingFormProps) => {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [answers, setAnswers] = useState<Record<string, string>>({});

    const fields: FormField[] = event.bookingForm?.fields ?? [];

    const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            await createBooking({
                slotId: slot.id,
                eventId: event.id,
                attendeeName: name,
                attendeeEmail: email,
                formSubmission:
                    fields.length > 0
                        ? {
                              answers: fields.map((f) => ({
                                  fieldId: f.id,
                                  fieldResponse: answers[f.id] ?? "",
                              })),
                          }
                        : undefined,
            });
            onSuccess();
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(error.response?.data?.message);
            } else {
                toast.error("Something went wrong");
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="flex flex-col gap-5">
            {/* Selected slot summary */}
            <div className="bg-primary/5 border border-primary/20 rounded-xl px-4 py-3">
                <p className="text-xs font-semibold text-primary uppercase tracking-wide mb-0.5">
                    Your appointment
                </p>
                <p className="text-sm font-medium text-base-content">
                    {formatDateTime(slot.startTime)} –{" "}
                    {new Date(slot.endTime).toLocaleTimeString("en-US", {
                        hour: "2-digit",
                        minute: "2-digit",
                    })}
                </p>
            </div>

            <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                {/* Name */}
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium">Your name</label>
                    <input
                        type="text"
                        className="input input-bordered w-full rounded-sm outline-none"
                        placeholder="John Doe"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                </div>

                {/* Email */}
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium">Email</label>
                    <input
                        type="email"
                        className="input input-bordered w-full rounded-sm outline-none"
                        placeholder="you@example.com"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>

                {/* Custom form fields */}
                {fields
                    .sort((a, b) => a.displayOrder - b.displayOrder)
                    .map((field) => (
                        <div key={field.id} className="flex flex-col gap-1.5">
                            <label className="text-sm font-medium">
                                {field.label}
                                {field.required && (
                                    <span className="text-error ml-1">*</span>
                                )}
                            </label>
                            {field.fieldType === "TEXTAREA" ? (
                                <textarea
                                    className="textarea textarea-bordered w-full resize-none rounded-sm outline-none"
                                    rows={3}
                                    value={answers[field.id] ?? ""}
                                    onChange={(e) =>
                                        setAnswers((prev) => ({
                                            ...prev,
                                            [field.id]: e.target.value,
                                        }))
                                    }
                                    required={field.required}
                                />
                            ) : (
                                <input
                                    type={
                                        field.fieldType === "NUMBER"
                                            ? "number"
                                            : "text"
                                    }
                                    className="input input-bordered w-full rounded-sm outline-none"
                                    value={answers[field.id] ?? ""}
                                    onChange={(e) =>
                                        setAnswers((prev) => ({
                                            ...prev,
                                            [field.id]: e.target.value,
                                        }))
                                    }
                                    required={field.required}
                                />
                            )}
                        </div>
                    ))}

                <div className="flex gap-2 pt-2">
                    <button
                        type="button"
                        className="btn btn-outline btn-sm flex-1"
                        onClick={onBack}
                    >
                        Back
                    </button>
                    <button
                        type="submit"
                        className="btn btn-primary btn-sm flex-1"
                        disabled={isLoading}
                    >
                        {isLoading ? (
                            <span className="loading loading-spinner loading-xs" />
                        ) : (
                            "Confirm booking"
                        )}
                    </button>
                </div>
            </form>
        </div>
    );
};
