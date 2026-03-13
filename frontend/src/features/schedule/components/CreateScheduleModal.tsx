import { zodResolver } from "@hookform/resolvers/zod";
import { Loader2Icon, XIcon } from "lucide-react";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import * as z from "zod";
import { useSchedulesContext } from "../context/schedulesContextStore";

import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogTitle } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { useApiError } from "@/hooks/useApiError";

interface Props {
    onClose: () => void;
    onSuccess: (id: string) => void;
}

const createScheduleSchema = z.object({
    name: z.string().trim().min(1, "Name is required."),
});

type CreateScheduleFormData = z.infer<typeof createScheduleSchema>;

const CreateScheduleModal = ({ onClose, onSuccess }: Props) => {
    const { create } = useSchedulesContext();
    const [isCreating, setIsCreating] = useState(false);
    const {
        register,
        watch,
        handleSubmit,
        formState: { errors },
    } = useForm<CreateScheduleFormData>({
        resolver: zodResolver(createScheduleSchema),
        defaultValues: {
            name: "",
        },
    });
    const name = watch("name");
    const handleError = useApiError();

    const onSubmit = async (data: CreateScheduleFormData) => {
        setIsCreating(true);
        try {
            const newSchedule = await create({
                name: data.name.trim(),
            });
            toast.success("Schedule created");
            onSuccess(newSchedule.id);
        } catch (error) {
            handleError(error);
            setIsCreating(false);
        }
    };

    const handleCreate = handleSubmit(onSubmit);

    return (
        <Dialog open>
            <DialogContent showCloseButton={false} className="sm:max-w-sm">
                <div className="flex items-center justify-between gap-4">
                    <DialogTitle>New schedule</DialogTitle>
                    <Button
                        type="button"
                        variant="ghost"
                        size="icon-sm"
                        onClick={onClose}
                        aria-label="Close"
                        className="rounded-full"
                    >
                        <XIcon className="size-4" />
                    </Button>
                </div>

                <div className="grid gap-2">
                    <label
                        htmlFor="schedule-name"
                        className="text-sm font-medium"
                    >
                        Name
                    </label>
                    <Input
                        id="schedule-name"
                        type="text"
                        placeholder="e.g. Work hours"
                        {...register("name")}
                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                e.preventDefault();
                                void handleCreate();
                            }
                        }}
                        autoFocus
                    />
                    {errors.name && (
                        <p className="text-sm text-destructive">
                            {errors.name.message}
                        </p>
                    )}
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
                        disabled={!name?.trim() || isCreating}
                        onClick={() => void handleCreate()}
                    >
                        {isCreating ? (
                            <Loader2Icon className="size-4 animate-spin" />
                        ) : (
                            "Create"
                        )}
                    </Button>
                </div>
            </DialogContent>
        </Dialog>
    );
};

export default CreateScheduleModal;
