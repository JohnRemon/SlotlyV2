import type { BookingFormFieldRequest } from "@/features/booking-page/types/BookingForms";
import { EventsApi } from "@/features/events/api/EventsApi";
import { useSchedules } from "@/features/schedule/hooks/useSchedules";
import axios from "axios";
import {
    FileText,
    Shield,
    Calendar,
    Clock,
    Settings,
    ChevronLeft,
    Copy,
    ExternalLink,
    Trash2,
    Plus,
    Loader2Icon,
} from "lucide-react";
import { useState, useEffect, useCallback } from "react";
import { useParams, useNavigate, useSearchParams } from "react-router";
import { toast } from "sonner";
import type { EventRequest, EventResponse } from "../types/Event";
import { AvailabilityTab } from "./AvailabilityTab";

import ConfirmDialog from "@/components/common/ConfirmDialog";
import ErrorMessage from "@/components/common/ErrorMessage";
import FormField from "@/components/common/FormField";
import LoadingSpinner from "@/components/common/LoadingSpinner";
import PageHeader from "@/components/common/PageHeader";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import { Switch } from "@/components/ui/switch";
import { Textarea } from "@/components/ui/textarea";
import { cn } from "@/lib/utils";

type Tab = "general" | "limits" | "availability" | "booking-form" | "advanced";

const TABS: { id: Tab; label: string; icon: React.ReactNode }[] = [
    { id: "general", label: "General", icon: <FileText className="w-4 h-4" /> },
    { id: "limits", label: "Limits", icon: <Shield className="w-4 h-4" /> },
    {
        id: "availability",
        label: "Availability",
        icon: <Calendar className="w-4 h-4" />,
    },
    {
        id: "booking-form",
        label: "Booking Form",
        icon: <Clock className="w-4 h-4" />,
    },
    {
        id: "advanced",
        label: "Advanced",
        icon: <Settings className="w-4 h-4" />,
    },
];

const TAB_DESCRIPTIONS: Record<Tab, string> = {
    general: "Core details that show up on the booking page.",
    limits: "Rules that control availability and booking behavior.",
    availability: "Pick which schedule generates slots for this event.",
    "booking-form": "Collect extra info from attendees at booking time.",
    advanced: "Tools and upcoming integrations for power users.",
};

const BOOKING_FIELD_TYPES = [
    { value: "TEXT", label: "Text" },
    { value: "PHONE", label: "Phone" },
] as const;

interface FormState {
    eventName: string;
    description: string;
    eventStart: string;
    eventEnd: string;
    slotDurationMinutes: number;
    bufferMinutes: number;
    minimumNoticeHours: number;
    maximumAdvanceDays: number;
    maxCapacity: number;
    maxSlotsPerUser: number;
    allowCancellations: boolean;
    isPublic: boolean;
    fields: BookingFormFieldRequest[];
}

const toDateTimeLocal = (iso: string) => {
    if (!iso) return "";
    return iso.slice(0, 16);
};

const getErrorMessage = (error: unknown) =>
    axios.isAxiosError(error)
        ? (error.response?.data?.message ?? "Something went wrong")
        : "Something went wrong";

const EventDetailPage = () => {
    const { id } = useParams() as { id: string };
    const navigate = useNavigate();

    const [event, setEvent] = useState<EventResponse | null>(null);
    const [isLoading, setLoading] = useState(true);
    const [isSaving, setSaving] = useState(false);
    const [isDeleting, setDeleting] = useState(false);
    const [searchParams, setSearchParams] = useSearchParams();
    const activeTab = (searchParams.get("tab") as Tab) ?? "general";
    const setActiveTab = (tab: Tab) => setSearchParams({ tab });
    const [form, setForm] = useState<FormState | null>(null);

    const { data: schedules = [], isLoading: schedulesLoading } = useSchedules();
    const [draftScheduleId, setDraftScheduleId] = useState<string>("");
    const [draftScheduleIsDefault, setDraftScheduleIsDefault] = useState(false);
    const [isUpdatingVisibility, setIsUpdatingVisibility] = useState(false);

    useEffect(() => {
        EventsApi.getById(Number(id))
            .then((res) => {
                const e = res.data.data;
                setEvent(e);
                setDraftScheduleId(String(e.scheduleId));
                setDraftScheduleIsDefault(e.scheduleIsDefault ?? false);
                setForm({
                    eventName: e.eventName ?? "",
                    description: e.description ?? "",
                    eventStart: toDateTimeLocal(e.eventStart),
                    eventEnd: toDateTimeLocal(e.eventEnd),
                    slotDurationMinutes:
                        e.availabilityRules?.slotDurationMinutes ?? 30,
                    bufferMinutes: e.availabilityRules?.bufferMinutes ?? 0,
                    minimumNoticeHours:
                        e.availabilityRules?.minimumNoticeHours ?? 0,
                    maximumAdvanceDays:
                        e.availabilityRules?.maximumAdvanceDays ?? 30,
                    maxCapacity: e.availabilityRules?.maxCapacity ?? 1,
                    maxSlotsPerUser: e.availabilityRules?.maxSlotsPerUser ?? 1,
                    allowCancellations:
                        e.availabilityRules?.allowsCancellations ?? true,
                    isPublic: e.availabilityRules?.isPublic ?? true,
                    fields:
                        e.bookingForm?.fields?.map((f) => ({
                            label: f.label,
                            fieldType: f.fieldType,
                            required: f.required,
                            displayOrder: f.displayOrder,
                        })) ?? [],
                });
            })
            .catch((error) => toast.error(getErrorMessage(error)))
            .finally(() => setLoading(false));
    }, [id]);

    const handleScheduleChange = async (scheduleId: string) => {
        const selected = schedules.find((s) => s.id === scheduleId);
        setDraftScheduleId(scheduleId);
        setDraftScheduleIsDefault(selected?.isDefault ?? false);
    };

    const handleVisibilityToggle = async (nextIsPublic: boolean) => {
        if (!event || !form || isUpdatingVisibility) return;

        const previousIsPublic = form.isPublic;
        set("isPublic", nextIsPublic);
        setIsUpdatingVisibility(true);

        try {
            const res = await EventsApi.updateAvailabilityRules(Number(id), {
                isPublic: nextIsPublic,
            });
            const updated = res.data.data;
            setEvent(updated);
            set(
                "isPublic",
                updated.availabilityRules?.isPublic ?? nextIsPublic,
            );
            toast.success("Visibility updated");
        } catch (error) {
            set("isPublic", previousIsPublic);
            toast.error(getErrorMessage(error));
        } finally {
            setIsUpdatingVisibility(false);
        }
    };

    const handleSave = async () => {
        if (!form) return;
        setSaving(true);
        try {
            let updated: EventResponse;

            if (activeTab === "booking-form") {
                await EventsApi.updateBookingForm(Number(id), {
                    fields: form.fields,
                });
                const eventRes = await EventsApi.getById(Number(id));
                updated = eventRes.data.data;
            } else if (activeTab === "availability") {
                const res = await EventsApi.updateSchedule(
                    Number(id),
                    draftScheduleId,
                );
                updated = res.data.data;
                setDraftScheduleId(String(updated.scheduleId));
                setDraftScheduleIsDefault(updated.scheduleIsDefault ?? false);
            } else {
                const payload: EventRequest = {
                    eventName: form.eventName,
                    description: form.description || undefined,
                    eventStart: new Date(form.eventStart).toISOString(),
                    eventEnd: new Date(form.eventEnd).toISOString(),
                    availabilityRules: {
                        slotDurationMinutes: form.slotDurationMinutes,
                        bufferMinutes: form.bufferMinutes,
                        minimumNoticeHours: form.minimumNoticeHours,
                        maximumAdvanceDays: form.maximumAdvanceDays,
                        maxCapacity: form.maxCapacity,
                        maxSlotsPerUser: form.maxSlotsPerUser,
                        allowsCancellations: form.allowCancellations,
                        isPublic: form.isPublic,
                    },
                };
                const res = await EventsApi.update(Number(id), payload);
                updated = res.data.data;
            }
            setEvent(updated);
            toast.success("Saved");
        } catch (error) {
            toast.error(getErrorMessage(error));
        } finally {
            setSaving(false);
        }
    };

    const handleDelete = async () => {
        if (!event) return;
        setDeleting(true);
        try {
            await EventsApi.delete(event.id);
            toast.success("Event deleted");
            navigate(-1);
        } catch (error) {
            toast.error(getErrorMessage(error));
        } finally {
            setDeleting(false);
        }
    };

    const set = useCallback(
        <K extends keyof FormState>(key: K, value: FormState[K]) => {
            setForm((prev) => (prev ? { ...prev, [key]: value } : prev));
        },
        [],
    );

    const addField = () => {
        setForm((prev) => {
            if (!prev) return prev;
            return {
                ...prev,
                fields: [
                    ...prev.fields,
                    {
                        label: "",
                        fieldType: "TEXT" as const,
                        required: false,
                        displayOrder: prev.fields.length,
                    },
                ],
            };
        });
    };

    const updateField = (
        index: number,
        patch: Partial<BookingFormFieldRequest>,
    ) => {
        setForm((prev) => {
            if (!prev) return prev;
            const fields = [...prev.fields];
            fields[index] = { ...fields[index], ...patch };
            return { ...prev, fields };
        });
    };

    const removeField = (index: number) => {
        setForm((prev) => {
            if (!prev) return prev;
            return {
                ...prev,
                fields: prev.fields.filter((_, i) => i !== index),
            };
        });
    };

    if (isLoading)
        return (
            <div className="flex justify-center py-16">
                <LoadingSpinner label="Loading event" size="lg" />
            </div>
        );

    if (!event || !form)
        return (
            <div className="mx-auto w-full max-w-3xl">
                <ErrorMessage
                    title="Event not found"
                    message="This event may have been deleted or you may not have access."
                    onRetry={() => navigate(-1)}
                    retryLabel="Back"
                />
            </div>
        );

    const bookingUrl = `${window.location.origin}/book/${event.shareableId}`;
    const activeTabMeta = TABS.find((tab) => tab.id === activeTab) ?? TABS[0];

    return (
        <div className="mx-auto flex w-full max-w-6xl flex-col gap-6">
            <PageHeader
                title={
                    <span className="inline-flex min-w-0 items-center gap-3">
                        <Button
                            type="button"
                            variant="outline"
                            size="icon"
                            onClick={() => navigate(-1)}
                            aria-label="Back"
                        >
                            <ChevronLeft className="size-4" />
                        </Button>
                        <span className="truncate">{event.eventName}</span>
                    </span>
                }
                description="Event settings"
                actions={
                    <div className="flex flex-wrap items-center justify-end gap-2">
                        <div className="flex items-center gap-2 rounded-xl border bg-background/40 px-2.5 py-1.5">
                            <Badge
                                variant={
                                    form.isPublic ? "secondary" : "outline"
                                }
                            >
                                {form.isPublic ? "Public" : "Private"}
                            </Badge>
                            <Switch
                                checked={form.isPublic}
                                disabled={isUpdatingVisibility}
                                aria-label="Toggle visibility"
                                onCheckedChange={(next) => {
                                    void handleVisibilityToggle(next);
                                }}
                            />
                        </div>

                        <Button
                            type="button"
                            variant="outline"
                            size="icon"
                            aria-label="Copy booking link"
                            onClick={() => {
                                navigator.clipboard.writeText(bookingUrl);
                                toast.success("Link copied!");
                            }}
                        >
                            <Copy className="size-4" />
                        </Button>

                        <a
                            href={bookingUrl}
                            target="_blank"
                            rel="noreferrer"
                            className={cn(
                                "inline-flex",
                                "group/button",
                                "size-8 items-center justify-center",
                                "rounded-lg border border-border bg-background",
                                "transition-colors hover:bg-muted",
                                "focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50",
                            )}
                            aria-label="Open booking page"
                            title="Open booking page"
                        >
                            <ExternalLink className="size-4" />
                        </a>

                        <ConfirmDialog
                            title="Delete event"
                            description={
                                <span>
                                    This permanently deletes{" "}
                                    <b>{event.eventName}</b> and cancels any
                                    existing bookings.
                                </span>
                            }
                            confirmLabel="Delete"
                            cancelLabel="Cancel"
                            confirmVariant="destructive"
                            disableConfirm={isDeleting}
                            onConfirm={handleDelete}
                            trigger={
                                <Button
                                    type="button"
                                    variant="outline"
                                    size="icon"
                                    className="text-destructive hover:bg-destructive/10 hover:text-destructive"
                                    aria-label="Delete event"
                                    disabled={isDeleting}
                                >
                                    {isDeleting ? (
                                        <Loader2Icon className="size-4 animate-spin" />
                                    ) : (
                                        <Trash2 className="size-4" />
                                    )}
                                </Button>
                            }
                        />

                        <Button
                            type="button"
                            onClick={handleSave}
                            disabled={isSaving}
                        >
                            {isSaving ? (
                                <>
                                    <Loader2Icon className="size-4 animate-spin" />
                                    Saving
                                </>
                            ) : (
                                "Save"
                            )}
                        </Button>
                    </div>
                }
            />

            <div className="grid gap-6 lg:grid-cols-[16rem_1fr]">
                <Card
                    size="sm"
                    className="gap-0 bg-card/60 supports-backdrop-filter:backdrop-blur-sm"
                >
                    <CardContent className="p-2">
                        <nav className="flex gap-1 overflow-x-auto lg:flex-col">
                            {TABS.map((tab) => (
                                <button
                                    key={tab.id}
                                    type="button"
                                    onClick={() => setActiveTab(tab.id)}
                                    className={cn(
                                        "flex items-center gap-2.5 rounded-xl px-3 py-2 text-sm font-medium transition-colors whitespace-nowrap",
                                        "text-muted-foreground hover:bg-muted/50 hover:text-foreground",
                                        activeTab === tab.id &&
                                            "bg-muted text-foreground ring-1 ring-foreground/10",
                                    )}
                                >
                                    {tab.icon}
                                    {tab.label}
                                </button>
                            ))}
                        </nav>
                    </CardContent>
                </Card>

                <Card className="bg-card/60 supports-backdrop-filter:backdrop-blur-sm">
                    <CardHeader className="border-b">
                        <CardTitle className="flex items-center gap-2">
                            <span className="text-muted-foreground">
                                {activeTabMeta.icon}
                            </span>
                            {activeTabMeta.label}
                        </CardTitle>
                        <CardDescription>
                            {TAB_DESCRIPTIONS[activeTab]}
                        </CardDescription>
                    </CardHeader>

                    <CardContent className="grid gap-5">
                        {activeTab === "general" && (
                            <>
                                <FormField label="Event name" required>
                                    <Input
                                        type="text"
                                        value={form.eventName}
                                        onChange={(e) =>
                                            set("eventName", e.target.value)
                                        }
                                    />
                                </FormField>

                                <FormField
                                    label="Description"
                                    hint="Optional description shown to attendees"
                                >
                                    <Textarea
                                        rows={3}
                                        value={form.description}
                                        onChange={(e) =>
                                            set("description", e.target.value)
                                        }
                                        placeholder="What is this event about?"
                                    />
                                </FormField>

                                <div className="grid gap-4 sm:grid-cols-2">
                                    <FormField
                                        label="Start date & time"
                                        required
                                    >
                                        <Input
                                            type="datetime-local"
                                            value={form.eventStart}
                                            onChange={(e) =>
                                                set(
                                                    "eventStart",
                                                    e.target.value,
                                                )
                                            }
                                        />
                                    </FormField>
                                    <FormField label="End date & time" required>
                                        <Input
                                            type="datetime-local"
                                            value={form.eventEnd}
                                            onChange={(e) =>
                                                set("eventEnd", e.target.value)
                                            }
                                        />
                                    </FormField>
                                </div>

                                <FormField
                                    label="Slot duration (minutes)"
                                    hint="How long each bookable time slot lasts"
                                    required
                                >
                                    <Input
                                        type="number"
                                        value={form.slotDurationMinutes}
                                        min={5}
                                        onChange={(e) =>
                                            set(
                                                "slotDurationMinutes",
                                                Number(e.target.value),
                                            )
                                        }
                                    />
                                </FormField>
                            </>
                        )}

                        {activeTab === "limits" && (
                            <>
                                <div className="grid gap-4 sm:grid-cols-2">
                                    <FormField
                                        label="Buffer time (minutes)"
                                        hint="Time between consecutive bookings"
                                    >
                                        <Input
                                            type="number"
                                            value={form.bufferMinutes}
                                            min={0}
                                            onChange={(e) =>
                                                set(
                                                    "bufferMinutes",
                                                    Number(e.target.value),
                                                )
                                            }
                                        />
                                    </FormField>

                                    <FormField
                                        label="Minimum notice (hours)"
                                        hint="How far in advance someone must book"
                                    >
                                        <Input
                                            type="number"
                                            value={form.minimumNoticeHours}
                                            min={0}
                                            onChange={(e) =>
                                                set(
                                                    "minimumNoticeHours",
                                                    Number(e.target.value),
                                                )
                                            }
                                        />
                                    </FormField>

                                    <FormField
                                        label="Maximum advance (days)"
                                        hint="How far ahead someone can book"
                                    >
                                        <Input
                                            type="number"
                                            value={form.maximumAdvanceDays}
                                            min={1}
                                            onChange={(e) =>
                                                set(
                                                    "maximumAdvanceDays",
                                                    Number(e.target.value),
                                                )
                                            }
                                        />
                                    </FormField>

                                    <FormField
                                        label="Max capacity per slot"
                                        hint="Max attendees per time slot"
                                    >
                                        <Input
                                            type="number"
                                            value={form.maxCapacity}
                                            min={1}
                                            onChange={(e) =>
                                                set(
                                                    "maxCapacity",
                                                    Number(e.target.value),
                                                )
                                            }
                                        />
                                    </FormField>

                                    <FormField
                                        label="Max slots per user"
                                        hint="How many slots one person can book"
                                    >
                                        <Input
                                            type="number"
                                            value={form.maxSlotsPerUser}
                                            min={1}
                                            onChange={(e) =>
                                                set(
                                                    "maxSlotsPerUser",
                                                    Number(e.target.value),
                                                )
                                            }
                                        />
                                    </FormField>
                                </div>

                                <div className="flex items-center justify-between gap-4 rounded-xl border bg-muted/20 p-4">
                                    <div className="min-w-0">
                                        <div className="text-sm font-medium">
                                            Allow cancellations
                                        </div>
                                        <div className="mt-0.5 text-xs text-muted-foreground">
                                            Let attendees cancel their bookings
                                        </div>
                                    </div>
                                    <Switch
                                        checked={form.allowCancellations}
                                        aria-label="Toggle cancellations"
                                        onCheckedChange={(next) =>
                                            set("allowCancellations", next)
                                        }
                                    />
                                </div>
                            </>
                        )}

                        {activeTab === "availability" && (
                            <AvailabilityTab
                                scheduleId={draftScheduleId}
                                scheduleIsDefault={draftScheduleIsDefault}
                                schedules={schedules}
                                schedulesLoading={schedulesLoading}
                                onScheduleChange={handleScheduleChange}
                            />
                        )}

                        {activeTab === "booking-form" && (
                            <>
                                <div className="flex items-center justify-between gap-3">
                                    <div className="text-sm font-medium">
                                        Fields
                                    </div>
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

                                {form.fields.length === 0 ? (
                                    <div className="rounded-2xl border border-dashed bg-card/40 p-8 text-center">
                                        <FileText className="mx-auto size-8 text-muted-foreground" />
                                        <div className="mt-3 text-sm font-medium">
                                            No custom fields yet
                                        </div>
                                        <div className="mt-1 text-xs text-muted-foreground">
                                            Add questions like phone number or a
                                            note.
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
                                        {form.fields.map((field, index) => (
                                            <Card
                                                key={`${field.displayOrder}-${field.label || "field"}`}
                                                size="sm"
                                                className="gap-3 bg-card/60"
                                            >
                                                <CardContent className="grid gap-3">
                                                    <div className="grid gap-3 sm:grid-cols-2">
                                                        <FormField label="Label">
                                                            <Input
                                                                type="text"
                                                                value={
                                                                    field.label
                                                                }
                                                                placeholder="e.g. Phone number"
                                                                onChange={(e) =>
                                                                    updateField(
                                                                        index,
                                                                        {
                                                                            label: e
                                                                                .target
                                                                                .value,
                                                                        },
                                                                    )
                                                                }
                                                            />
                                                        </FormField>

                                                        <FormField label="Type">
                                                            <Select
                                                                value={
                                                                    field.fieldType
                                                                }
                                                                onValueChange={(
                                                                    value,
                                                                ) => {
                                                                    if (
                                                                        value ===
                                                                            "TEXT" ||
                                                                        value ===
                                                                            "PHONE"
                                                                    ) {
                                                                        updateField(
                                                                            index,
                                                                            {
                                                                                fieldType:
                                                                                    value,
                                                                            },
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
                                                                        (
                                                                            option,
                                                                        ) => (
                                                                            <SelectItem
                                                                                key={
                                                                                    option.value
                                                                                }
                                                                                value={
                                                                                    option.value
                                                                                }
                                                                            >
                                                                                {
                                                                                    option.label
                                                                                }
                                                                            </SelectItem>
                                                                        ),
                                                                    )}
                                                                </SelectContent>
                                                            </Select>
                                                        </FormField>
                                                    </div>

                                                    <div className="flex items-center justify-between gap-3">
                                                        <div className="flex items-center gap-2 text-xs text-muted-foreground">
                                                            <span>
                                                                Required
                                                            </span>
                                                            <Switch
                                                                checked={
                                                                    field.required
                                                                }
                                                                aria-label="Toggle required"
                                                                onCheckedChange={(
                                                                    next,
                                                                ) =>
                                                                    updateField(
                                                                        index,
                                                                        {
                                                                            required:
                                                                                next,
                                                                        },
                                                                    )
                                                                }
                                                            />
                                                        </div>
                                                        <Button
                                                            type="button"
                                                            variant="ghost"
                                                            size="icon-sm"
                                                            className="text-destructive hover:bg-destructive/10 hover:text-destructive"
                                                            onClick={() =>
                                                                removeField(
                                                                    index,
                                                                )
                                                            }
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
                        )}

                        {activeTab === "advanced" && (
                            <>
                                <FormField
                                    label="Booking link"
                                    hint="Share this URL with attendees"
                                >
                                    <div className="flex items-center gap-2">
                                        <Input
                                            type="text"
                                            value={bookingUrl}
                                            readOnly
                                        />
                                        <Button
                                            type="button"
                                            variant="outline"
                                            size="icon"
                                            aria-label="Copy link"
                                            onClick={() => {
                                                navigator.clipboard.writeText(
                                                    bookingUrl,
                                                );
                                                toast.success("Link copied!");
                                            }}
                                        >
                                            <Copy className="size-4" />
                                        </Button>
                                        <a
                                            href={bookingUrl}
                                            target="_blank"
                                            rel="noreferrer"
                                            className={cn(
                                                "inline-flex",
                                                "group/button",
                                                "size-8 items-center justify-center",
                                                "rounded-lg border border-border bg-background",
                                                "transition-colors hover:bg-muted",
                                                "focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50",
                                            )}
                                            aria-label="Open booking page"
                                        >
                                            <ExternalLink className="size-4" />
                                        </a>
                                    </div>
                                </FormField>

                                <div className="rounded-2xl border border-dashed bg-card/40 p-8 text-center">
                                    <Calendar className="mx-auto size-8 text-muted-foreground" />
                                    <div className="mt-3 text-sm font-medium">
                                        Calendar sync coming soon
                                    </div>
                                    <div className="mt-1 text-xs text-muted-foreground">
                                        Connect Google Calendar or Outlook to
                                        sync bookings automatically.
                                    </div>
                                </div>
                            </>
                        )}
                    </CardContent>
                </Card>
            </div>
        </div>
    );
};

export default EventDetailPage;
