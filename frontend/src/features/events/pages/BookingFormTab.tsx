import FormField from "@/components/common/FormField";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import { Switch } from "@/components/ui/switch";
import { FileText, Plus, Trash2 } from "lucide-react";
import { useFieldArray, useFormContext } from "react-hook-form";
import type { EventFormData } from "../schema/schema";

const BOOKING_FIELD_TYPES = [
    { value: "TEXT", label: "Text" },
    { value: "PHONE", label: "Phone" },
] as const;

export const BookingFormTab = () => {
    const {
        register,
        control,
        watch,
        setValue,
        formState: { errors },
    } = useFormContext<EventFormData>();

    const { fields, append, remove } = useFieldArray({
        control,
        name: "fields",
    });

    const addField = () => {
        append({
            label: "",
            fieldType: "TEXT",
            required: false,
            displayOrder: fields.length,
        });
    };

    return (
        <>
            <div className="flex items-center justify-between gap-3">
                <div className="text-sm font-medium">Fields</div>
                <Button
                    type="button"
                    variant="outline"
                    size="sm"
                    onClick={addField}
                >
                    <Plus className="size-4" />
                    Add field
                </Button>
            </div>

            {fields.length === 0 ? (
                <div className="rounded-2xl border border-dashed bg-card/40 p-8 text-center">
                    <FileText className="mx-auto size-8 text-muted-foreground" />
                    <div className="mt-3 text-sm font-medium">
                        No custom fields yet
                    </div>
                    <div className="mt-1 text-xs text-muted-foreground">
                        Add questions like phone number or a note.
                    </div>
                    <div className="mt-4">
                        <Button
                            type="button"
                            variant="outline"
                            size="sm"
                            onClick={addField}
                        >
                            <Plus className="size-4" />
                            Add your first field
                        </Button>
                    </div>
                </div>
            ) : (
                <div className="grid gap-3">
                    {fields.map((field, index) => (
                        <Card
                            key={field.id}
                            size="sm"
                            className="gap-3 bg-card/60"
                        >
                            <CardContent className="grid gap-3">
                                <div className="grid gap-3 sm:grid-cols-2">
                                    <div className="grid gap-2">
                                        <FormField label="Label">
                                            <Input
                                                type="text"
                                                placeholder="e.g. Phone number"
                                                {...register(
                                                    `fields.${index}.label`,
                                                )}
                                            />
                                        </FormField>
                                        {errors.fields?.[index]?.label && (
                                            <p className="text-sm text-destructive">
                                                {
                                                    errors.fields[index].label
                                                        .message
                                                }
                                            </p>
                                        )}
                                    </div>

                                    <FormField label="Type">
                                        <Select
                                            value={watch(
                                                `fields.${index}.fieldType`,
                                            )}
                                            onValueChange={(value) => {
                                                if (
                                                    value === "TEXT" ||
                                                    value === "PHONE"
                                                ) {
                                                    setValue(
                                                        `fields.${index}.fieldType`,
                                                        value,
                                                    );
                                                }
                                            }}
                                        >
                                            <SelectTrigger
                                                className="w-full"
                                                size="sm"
                                            >
                                                <SelectValue placeholder="Select type" />
                                            </SelectTrigger>
                                            <SelectContent align="start">
                                                {BOOKING_FIELD_TYPES.map(
                                                    (option) => (
                                                        <SelectItem
                                                            key={option.value}
                                                            value={option.value}
                                                        >
                                                            {option.label}
                                                        </SelectItem>
                                                    ),
                                                )}
                                            </SelectContent>
                                        </Select>
                                    </FormField>
                                </div>

                                <div className="flex items-center justify-between gap-3">
                                    <div className="flex items-center gap-2 text-xs text-muted-foreground">
                                        <span>Required</span>
                                        <Switch
                                            checked={watch(
                                                `fields.${index}.required`,
                                            )}
                                            aria-label="Toggle required"
                                            onCheckedChange={(next) =>
                                                setValue(
                                                    `fields.${index}.required`,
                                                    next,
                                                )
                                            }
                                        />
                                    </div>
                                    <Button
                                        type="button"
                                        variant="ghost"
                                        size="icon-sm"
                                        className="text-destructive hover:bg-destructive/10 hover:text-destructive"
                                        onClick={() => remove(index)}
                                        aria-label="Remove field"
                                    >
                                        <Trash2 className="size-4" />
                                    </Button>
                                </div>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            )}
        </>
    );
};
