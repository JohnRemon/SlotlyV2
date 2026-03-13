import FormField from "@/components/common/FormField";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { useFormContext } from "react-hook-form";
import type { EventFormData } from "../schema/schema";

export const GeneralTab = () => {
    const {
        register,
        formState: { errors },
    } = useFormContext<EventFormData>();

    return (
        <>
            <div className="grid gap-2">
                <FormField label="Event name" required>
                    <Input type="text" {...register("eventName")} />
                </FormField>
                {errors.eventName && (
                    <p className="text-sm text-destructive">
                        {errors.eventName.message}
                    </p>
                )}
            </div>

            <div className="grid gap-2">
                <FormField
                    label="Description"
                    hint="Optional description shown to attendees"
                >
                    <Textarea
                        rows={3}
                        placeholder="What is this event about?"
                        {...register("description")}
                    />
                </FormField>
            </div>

            <div className="grid gap-4 sm:grid-cols-2">
                <div className="grid gap-2">
                    <FormField label="Start date & time" required>
                        <Input
                            type="datetime-local"
                            {...register("eventStart")}
                        />
                    </FormField>
                    {errors.eventStart && (
                        <p className="text-sm text-destructive">
                            {errors.eventStart.message}
                        </p>
                    )}
                </div>

                <div className="grid gap-2">
                    <FormField label="End date & time" required>
                        <Input
                            type="datetime-local"
                            {...register("eventEnd")}
                        />
                    </FormField>
                    {errors.eventEnd && (
                        <p className="text-sm text-destructive">
                            {errors.eventEnd.message}
                        </p>
                    )}
                </div>
            </div>

            <div className="grid gap-2">
                <FormField
                    label="Slot duration (minutes)"
                    hint="How long each bookable time slot lasts"
                    required
                >
                    <Input
                        type="number"
                        min={5}
                        placeholder="0"
                        {...register("slotDurationMinutes", {
                            valueAsNumber: true,
                        })}
                    />
                </FormField>
                {errors.slotDurationMinutes && (
                    <p className="text-sm text-destructive">
                        {errors.slotDurationMinutes.message}
                    </p>
                )}
            </div>
        </>
    );
};
