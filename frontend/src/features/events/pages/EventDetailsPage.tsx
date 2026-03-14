import { EventsApi } from "@/features/events/api/EventsApi";
import { useSchedules } from "@/features/schedule/hooks/useSchedules";
import {
    Calendar,
    ChevronLeft,
    Clock,
    Copy,
    ExternalLink,
    FileText,
    Loader2Icon,
    Settings,
    Shield,
    Trash2,
} from "lucide-react";
import { useEffect, useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import { useNavigate, useParams, useSearchParams } from "react-router";
import { toast } from "sonner";
import type { EventRequest, EventResponse } from "../types/Event";
import { AvailabilityTab } from "./AvailabilityTab";
import { AdvancedTab } from "./AdvancedTab";
import { BookingFormTab } from "./BookingFormTab";
import { GeneralTab } from "./GeneralTab";
import { LimitsTab } from "./LimitsTab";

import ConfirmDialog from "@/components/common/ConfirmDialog";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Switch } from "@/components/ui/switch";
import { useApiError } from "@/hooks/useApiError";
import { cn } from "@/lib/utils";
import { zodResolver } from "@hookform/resolvers/zod";
import { type EventFormData, eventFormSchema } from "../schema/schema";

type Tab = "general" | "limits" | "availability" | "booking-form" | "advanced";

const TABS: { id: Tab; label: string; icon: React.ReactNode }[] = [
    { id: "general", label: "General", icon: <FileText className="size-4" /> },
    { id: "limits", label: "Limits", icon: <Shield className="size-4" /> },
    {
        id: "availability",
        label: "Availability",
        icon: <Calendar className="size-4" />,
    },
    {
        id: "booking-form",
        label: "Booking Form",
        icon: <Clock className="size-4" />,
    },
    {
        id: "advanced",
        label: "Advanced",
        icon: <Settings className="size-4" />,
    },
];

const TAB_DESCRIPTIONS: Record<Tab, string> = {
    general: "Core details that show up on the booking page.",
    limits: "Rules that control availability and booking behavior.",
    availability: "Pick which schedule generates slots for this event.",
    "booking-form": "Collect extra info from attendees at booking time.",
    advanced: "Tools and upcoming integrations for power users.",
};

const EventDetailPage = () => {
    const { id } = useParams() as { id: string };
    const navigate = useNavigate();
    const handleError = useApiError();

    const [event, setEvent] = useState<EventResponse | null>(null);
    const [isLoading, setLoading] = useState(true);
    const [isSaving, setSaving] = useState(false);
    const [isDeleting, setDeleting] = useState(false);
    const [searchParams, setSearchParams] = useSearchParams();
    const activeTab = (searchParams.get("tab") as Tab) ?? "general";
    const setActiveTab = (tab: Tab) => setSearchParams({ tab });
    const [isUpdatingVisibility, setIsUpdatingVisibility] = useState(false);

    const { data: schedules = [], isLoading: schedulesLoading } =
        useSchedules();

    const form = useForm<EventFormData>({
        resolver: zodResolver(eventFormSchema),
        defaultValues: {
            eventName: "",
            description: "",
            slotDurationMinutes: 30,
            bufferMinutes: 0,
            minimumNoticeHours: 0,
            maximumAdvanceDays: 30,
            maxCapacity: 1,
            maxSlotsPerUser: 1,
            allowCancellations: true,
            isPublic: true,
            fields: [],
        },
    });

    useEffect(() => {
        EventsApi.getById(Number(id))
            .then((res) => {
                const e = res.data.data;
                setEvent(e);
                form.reset({
                    eventName: e.eventName ?? "",
                    description: e.description ?? "",
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
            .catch((error) => handleError(error))
            .finally(() => setLoading(false));
    }, [id, handleError, form]);

    const handleScheduleChange = async (scheduleId: string) => {
        const selected = schedules.find((s) => s.id === scheduleId);
        if (selected) {
            setEvent((prev) => (prev ? { ...prev, schedule: selected } : prev));
        }
    };

    const handleVisibilityToggle = async (nextIsPublic: boolean) => {
        if (!event || isUpdatingVisibility) return;
        const previousIsPublic = form.getValues("isPublic");
        form.setValue("isPublic", nextIsPublic);
        setIsUpdatingVisibility(true);
        try {
            const res = await EventsApi.updateAvailabilityRules(Number(id), {
                isPublic: nextIsPublic,
            });
            const updated = res.data.data;
            setEvent(updated);
            form.setValue(
                "isPublic",
                updated.availabilityRules?.isPublic ?? nextIsPublic,
            );
            toast.success("Visibility updated");
        } catch (error) {
            form.setValue("isPublic", previousIsPublic);
            handleError(error);
        } finally {
            setIsUpdatingVisibility(false);
        }
    };

    const handleSave = form.handleSubmit(async (data) => {
        if (!event) return;
        setSaving(true);
        try {
            let updated: EventResponse;

            if (activeTab === "booking-form") {
                await EventsApi.updateBookingForm(Number(id), {
                    fields: data.fields,
                });
                const eventRes = await EventsApi.getById(Number(id));
                updated = eventRes.data.data;
            } else if (activeTab === "availability") {
                const res = await EventsApi.updateSchedule(
                    Number(id),
                    event.schedule.id,
                );
                updated = res.data.data;
            } else {
                const payload: EventRequest = {
                    eventName: data.eventName,
                    description: data.description || undefined,
                    availabilityRules: {
                        slotDurationMinutes: data.slotDurationMinutes,
                        bufferMinutes: data.bufferMinutes,
                        minimumNoticeHours: data.minimumNoticeHours,
                        maximumAdvanceDays: data.maximumAdvanceDays,
                        maxCapacity: data.maxCapacity,
                        maxSlotsPerUser: data.maxSlotsPerUser,
                        allowsCancellations: data.allowCancellations,
                        isPublic: data.isPublic,
                    },
                };
                const res = await EventsApi.update(Number(id), payload);
                updated = res.data.data;
            }
            setEvent(updated);
            toast.success("Saved");
        } catch (error) {
            handleError(error);
        } finally {
            setSaving(false);
        }
    });

    const handleDelete = async () => {
        if (!event) return;
        setDeleting(true);
        try {
            await EventsApi.delete(event.id);
            toast.success("Event deleted");
            navigate(-1);
        } catch (error) {
            handleError(error);
        } finally {
            setDeleting(false);
        }
    };

    const bookingUrl = `${window.location.origin}/book/${event?.shareableId}`;
    const activeTabMeta = TABS.find((tab) => tab.id === activeTab) ?? TABS[0];
    const isPublic = form.watch("isPublic");

    if (isLoading) {
        return (
            <div className="flex justify-center py-16">
                <Loader2Icon className="size-5 animate-spin text-muted-foreground" />
            </div>
        );
    }

    if (!event) {
        return (
            <div className="flex justify-center py-16">
                <div className="flex flex-col items-center gap-2 text-center">
                    <p className="text-sm font-semibold tracking-[-0.01em]">
                        Event not found
                    </p>
                    <p className="mt-1 text-xs text-muted-foreground">
                        This event may have been deleted or you may not have
                        access.
                    </p>
                    <Button
                        type="button"
                        variant="outline"
                        className="mt-2"
                        onClick={() => navigate(-1)}
                    >
                        Go back
                    </Button>
                </div>
            </div>
        );
    }

    return (
        <FormProvider {...form}>
            <div className="mx-auto flex w-full max-w-6xl flex-col gap-6">
                <div className="flex flex-wrap items-center justify-between gap-4">
                    <div className="flex min-w-0 items-center gap-3">
                        <Button
                            type="button"
                            variant="outline"
                            size="icon"
                            onClick={() => navigate(-1)}
                            aria-label="Back"
                        >
                            <ChevronLeft className="size-4" />
                        </Button>
                        <div className="min-w-0">
                            <h1 className="truncate text-base font-semibold tracking-[-0.01em]">
                                {event.eventName}
                            </h1>
                            <p className="mt-0.5 text-xs text-muted-foreground">
                                Event settings
                            </p>
                        </div>
                    </div>

                    <div className="flex flex-wrap items-center gap-2">
                        <div className="flex items-center gap-2 rounded-xl border bg-background/40 px-2.5 py-1.5">
                            <Badge variant={isPublic ? "secondary" : "outline"}>
                                {isPublic ? "Public" : "Private"}
                            </Badge>
                            <Switch
                                checked={isPublic}
                                disabled={isUpdatingVisibility}
                                aria-label="Toggle visibility"
                                onCheckedChange={(next) =>
                                    void handleVisibilityToggle(next)
                                }
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
                                "inline-flex size-8 items-center justify-center",
                                "rounded-lg border border-border bg-background",
                                "transition-colors hover:bg-muted",
                                "focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50",
                            )}
                            aria-label="Open booking page"
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
                </div>

                <div className="grid gap-6 lg:grid-cols-[16rem_1fr]">
                    <div className="rounded-2xl border bg-card/40 p-2 shadow-sm ring-1 ring-foreground/5 supports-backdrop-filter:backdrop-blur-sm">
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
                    </div>

                    <div className="rounded-2xl border bg-card/40 shadow-sm ring-1 ring-foreground/5 supports-backdrop-filter:backdrop-blur-sm">
                        <div className="border-b px-6 py-4">
                            <div className="flex items-center gap-2">
                                <span className="text-muted-foreground">
                                    {activeTabMeta.icon}
                                </span>
                                <p className="text-sm font-semibold tracking-[-0.01em]">
                                    {activeTabMeta.label}
                                </p>
                            </div>
                            <p className="mt-0.5 text-xs text-muted-foreground">
                                {TAB_DESCRIPTIONS[activeTab]}
                            </p>
                        </div>

                        <div className="grid gap-5 p-6">
                            {activeTab === "general" && <GeneralTab />}
                            {activeTab === "limits" && <LimitsTab />}
                            {activeTab === "availability" && (
                                <AvailabilityTab
                                    schedule={event.schedule}
                                    schedules={schedules}
                                    schedulesLoading={schedulesLoading}
                                    onScheduleChange={handleScheduleChange}
                                />
                            )}
                            {activeTab === "booking-form" && <BookingFormTab />}
                            {activeTab === "advanced" && (
                                <AdvancedTab bookingUrl={bookingUrl} />
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </FormProvider>
    );
};

export default EventDetailPage;
