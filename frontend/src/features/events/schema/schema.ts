import * as z from "zod";

export const eventFormSchema = z.object({
    eventName: z.string().min(1, "Event name is required."),
    description: z.string().optional(),
    eventStart: z.string().min(1, "Start date is required."),
    eventEnd: z.string().min(1, "End date is required."),
    slotDurationMinutes: z.number().min(5, "Must be at least 5 minutes."),
    bufferMinutes: z.number().min(0, "Cannot be negative."),
    minimumNoticeHours: z.number().min(0, "Cannot be negative."),
    maximumAdvanceDays: z.number().min(1, "Must be at least 1 day."),
    maxCapacity: z.number().min(1, "Must be at least 1."),
    maxSlotsPerUser: z.number().min(1, "Must be at least 1."),
    allowCancellations: z.boolean(),
    isPublic: z.boolean(),
    fields: z.array(
        z.object({
            label: z.string().min(1, "Label is required."),
            fieldType: z.enum(["TEXT", "PHONE"]),
            required: z.boolean(),
            displayOrder: z.number(),
        }),
    ),
});

export type EventFormData = z.infer<typeof eventFormSchema>;

export type FormState = EventFormData;

export const toDateTimeLocal = (iso: string) => {
    if (!iso) return "";
    return iso.slice(0, 16);
};
