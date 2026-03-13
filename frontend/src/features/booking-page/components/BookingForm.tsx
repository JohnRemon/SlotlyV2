import type { PublicEventResponse } from "@/features/events/types/Event";
import type { SlotResponse } from "@/features/slots/types/Slots";
import { zodResolver } from "@hookform/resolvers/zod";
import { Loader2Icon } from "lucide-react";
import { useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import type { BookingFormFieldResponse } from "../types/BookingForms";
import { BookingsApi } from "@/features/bookings/api/BookingsApi";

import FormFieldWrapper from "@/components/common/FormField";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

interface BookingFormProps {
    event: PublicEventResponse;
    slot: SlotResponse;
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

const buildBookingFormSchema = (fields: BookingFormFieldResponse[]) =>
    z.object({
        attendeeName: z.string().trim().min(1, "Your name is required."),
        attendeeEmail: z.email("Enter a valid email address."),
        answers: z
            .record(z.string(), z.string())
            .superRefine((answers, ctx) => {
                fields.forEach((field) => {
                    if (!field.required) {
                        return;
                    }

                    if ((answers[field.id] ?? "").trim().length === 0) {
                        ctx.addIssue({
                            code: "custom",
                            path: [field.id],
                            message: `${field.label} is required.`,
                        });
                    }
                });
            }),
    });

type BookingFormValues = {
    attendeeName: string;
    attendeeEmail: string;
    answers: Record<string, string>;
};

export const BookingForm = ({
    event,
    slot,
    onSuccess,
    onBack,
}: BookingFormProps) => {
    const [isLoading, setIsLoading] = useState(false);

    const fields: BookingFormFieldResponse[] = useMemo(
        () => event.bookingForm?.fields.fields ?? [],
        [event.bookingForm?.fields.fields],
    );

    const orderedFields = useMemo(
        () => [...fields].sort((a, b) => a.displayOrder - b.displayOrder),
        [fields],
    );

    const bookingFormSchema = useMemo(
        () => buildBookingFormSchema(orderedFields),
        [orderedFields],
    );

    const defaultAnswers = useMemo(
        () =>
            Object.fromEntries(orderedFields.map((field) => [field.id, ""])),
        [orderedFields],
    );

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<BookingFormValues>({
        resolver: zodResolver(bookingFormSchema),
        defaultValues: {
            attendeeName: "",
            attendeeEmail: "",
            answers: defaultAnswers,
        },
    });

    const handleBookingSubmit = async (data: BookingFormValues) => {
        setIsLoading(true);
        try {
            await BookingsApi.create({
                slotId: slot.id,
                attendeeName: data.attendeeName,
                attendeeEmail: data.attendeeEmail,
                formSubmission:
                    orderedFields.length > 0
                        ? {
                              answers: orderedFields.map((f) => ({
                                  fieldId: f.id,
                                  fieldResponse: data.answers[f.id] ?? "",
                              })),
                          }
                        : undefined,
            });
            onSuccess();
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="flex flex-col gap-5">
            <div className="rounded-xl border border-primary/20 bg-primary/10 px-4 py-3">
                <p className="text-xs font-semibold text-primary uppercase tracking-wide mb-0.5">
                    Your appointment
                </p>
                <p className="text-sm font-medium">
                    {formatDateTime(slot.startTime)} –{" "}
                    {new Date(slot.endTime).toLocaleTimeString("en-US", {
                        hour: "2-digit",
                        minute: "2-digit",
                    })}
                </p>
            </div>

            <form
                onSubmit={handleSubmit(handleBookingSubmit)}
                className="flex flex-col gap-4"
                noValidate
            >
                <FormFieldWrapper
                    id="booking-name"
                    label="Your name"
                    required
                    hint={
                        errors.attendeeName?.message ? (
                            <span className="text-destructive">
                                {errors.attendeeName.message}
                            </span>
                        ) : undefined
                    }
                >
                    <Input
                        id="booking-name"
                        type="text"
                        placeholder="John Doe"
                        {...register("attendeeName")}
                    />
                </FormFieldWrapper>

                <FormFieldWrapper
                    id="booking-email"
                    label="Email"
                    required
                    hint={
                        errors.attendeeEmail?.message ? (
                            <span className="text-destructive">
                                {errors.attendeeEmail.message}
                            </span>
                        ) : undefined
                    }
                >
                    <Input
                        id="booking-email"
                        type="email"
                        placeholder="you@example.com"
                        {...register("attendeeEmail")}
                    />
                </FormFieldWrapper>

                {orderedFields.map((field) => (
                        <FormFieldWrapper
                            key={field.id}
                            id={`booking-field-${field.id}`}
                            label={field.label}
                            required={field.required}
                            hint={
                                errors.answers?.[field.id]?.message ? (
                                    <span className="text-destructive">
                                        {errors.answers[field.id]?.message}
                                    </span>
                                ) : undefined
                            }
                        >
                            <Input
                                id={`booking-field-${field.id}`}
                                type={
                                    field.fieldType === "PHONE"
                                        ? "tel"
                                        : "text"
                                }
                                {...register(`answers.${field.id}`)}
                            />
                        </FormFieldWrapper>
                    ))}

                <div className="flex gap-2 pt-2">
                    <Button
                        type="button"
                        variant="outline"
                        className="flex-1"
                        onClick={onBack}
                    >
                        Back
                    </Button>
                    <Button
                        type="submit"
                        className="flex-1"
                        disabled={isLoading}
                    >
                        {isLoading ? (
                            <Loader2Icon className="size-4 animate-spin" />
                        ) : (
                            "Confirm booking"
                        )}
                    </Button>
                </div>
            </form>
        </div>
    );
};
