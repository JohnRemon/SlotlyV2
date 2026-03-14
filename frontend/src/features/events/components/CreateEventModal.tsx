import { useState } from "react";
import type { EventRequest } from "../types/Event";

import FormField from "@/components/common/FormField";
import { Button } from "@/components/ui/button";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { zodResolver } from "@hookform/resolvers/zod";
import { Loader2Icon } from "lucide-react";
import { useForm } from "react-hook-form";
import * as z from "zod";

const createEventSchema = z.object({
    eventName: z.string().trim().min(1, "Event name is required."),
    description: z.string(),
    slotDuration: z
        .number()
        .int("Slot duration must be a whole number.")
        .min(5, "Slot duration must be at least 5 minutes.")
        .multipleOf(5, "Slot duration must be in 5-minute increments."),
});

type CreateEventFormInput = z.input<typeof createEventSchema>;
type CreateEventFormData = z.output<typeof createEventSchema>;

interface CreateEventModalProps {
    open: boolean;
    onClose: () => void;
    onCreate: (payload: EventRequest) => Promise<void>;
}

const CreateEventModal = ({
    open,
    onClose,
    onCreate,
}: CreateEventModalProps) => {
    const [isLoading, setIsLoading] = useState(false);
    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<CreateEventFormInput, unknown, CreateEventFormData>({
        resolver: zodResolver(createEventSchema),
        defaultValues: {
            eventName: "",
            description: "",
            slotDuration: 30,
        },
    });

    const onSubmit = async (data: CreateEventFormData) => {
        setIsLoading(true);
        try {
            await onCreate({
                eventName: data.eventName.trim(),
                description: data.description,
                availabilityRules: {
                    slotDurationMinutes: data.slotDuration,
                },
            });
            onClose();
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Dialog
            open={open}
            onOpenChange={(next) => {
                if (!next) onClose();
            }}
        >
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>New event</DialogTitle>
                    <DialogDescription>
                        Create a bookable event with a default slot duration.
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} className="grid gap-4">
                    <FormField label="Event name" required>
                        <Input
                            type="text"
                            placeholder="e.g. 30 min meeting"
                            {...register("eventName")}
                        />
                        {errors.eventName && (
                            <p className="text-sm text-destructive">
                                {errors.eventName.message}
                            </p>
                        )}
                    </FormField>

                    <FormField
                        label="Description"
                        hint="Optional description shown to attendees"
                    >
                        <Textarea
                            rows={2}
                            placeholder="What is this event about?"
                            {...register("description")}
                        />
                    </FormField>

                    <FormField
                        label="Slot duration (minutes)"
                        hint="You can adjust rules later"
                        required
                    >
                        <Input
                            type="number"
                            min={5}
                            step={5}
                            {...register("slotDuration", {
                                valueAsNumber: true,
                            })}
                        />
                        {errors.slotDuration && (
                            <p className="text-sm text-destructive">
                                {errors.slotDuration.message}
                            </p>
                        )}
                    </FormField>

                    <DialogFooter>
                        <Button
                            type="button"
                            variant="outline"
                            onClick={onClose}
                        >
                            Cancel
                        </Button>
                        <Button type="submit" disabled={isLoading}>
                            {isLoading ? (
                                <>
                                    <Loader2Icon className="size-4 animate-spin" />
                                    Creating
                                </>
                            ) : (
                                "Create event"
                            )}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
};

export default CreateEventModal;
