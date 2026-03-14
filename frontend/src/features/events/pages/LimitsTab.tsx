import FormField from "@/components/common/FormField";
import { Input } from "@/components/ui/input";
import { Switch } from "@/components/ui/switch";
import { useFormContext } from "react-hook-form";
import type { EventFormData } from "../schema/schema";

const NumberField = ({
    name,
    label,
    hint,
    min = 0,
}: {
    name: keyof Pick<
        EventFormData,
        | "bufferMinutes"
        | "minimumNoticeHours"
        | "maximumAdvanceDays"
        | "maxCapacity"
        | "maxSlotsPerUser"
    >;
    label: string;
    hint?: string;
    min?: number;
}) => {
    const {
        register,
        formState: { errors },
    } = useFormContext<EventFormData>();

    return (
        <div className="grid gap-2">
            <FormField label={label} hint={hint}>
                <Input
                    type="number"
                    min={min}
                    placeholder="0"
                    {...register(name, { valueAsNumber: true })}
                />
            </FormField>
            {errors[name] && (
                <p className="text-xs text-destructive">
                    {errors[name]?.message}
                </p>
            )}
        </div>
    );
};

export const LimitsTab = () => {
    const { watch, setValue } = useFormContext<EventFormData>();

    return (
        <>
            <div className="grid gap-4 sm:grid-cols-2">
                <NumberField
                    name="bufferMinutes"
                    label="Buffer time (minutes)"
                    hint="Time between consecutive bookings"
                    min={0}
                />
                <NumberField
                    name="minimumNoticeHours"
                    label="Minimum notice (hours)"
                    hint="How far in advance someone must book"
                    min={0}
                />
                <NumberField
                    name="maximumAdvanceDays"
                    label="Maximum advance (days)"
                    hint="How far ahead someone can book"
                    min={1}
                />
                <NumberField
                    name="maxCapacity"
                    label="Max capacity per slot"
                    hint="Max attendees per time slot"
                    min={1}
                />
                <NumberField
                    name="maxSlotsPerUser"
                    label="Max slots per user"
                    hint="How many slots one person can book"
                    min={1}
                />
            </div>

            <div className="flex items-center justify-between gap-4 rounded-2xl border bg-muted/20 p-4 ring-1 ring-foreground/5">
                <div className="min-w-0">
                    <p className="text-sm font-medium">Allow cancellations</p>
                    <p className="mt-0.5 text-xs text-muted-foreground">
                        Let attendees cancel their bookings
                    </p>
                </div>
                <Switch
                    checked={watch("allowCancellations")}
                    aria-label="Toggle cancellations"
                    onCheckedChange={(next) =>
                        setValue("allowCancellations", next)
                    }
                />
            </div>
        </>
    );
};
