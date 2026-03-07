import axios from "axios";
import { useCallback, useEffect, useState } from "react";
import toast from "react-hot-toast";
import { useNavigate, useParams, useSearchParams } from "react-router";
import {
    getEvent,
    updateEvent,
    updateBookingForm,
    deleteEvent,
} from "../api/EventsApi";
import type { Event, EventRequest } from "../types/Event";
import type { BookingFormFieldRequest } from "../../bookings/types/Booking";
import {
    Calendar,
    Clock,
    FileText,
    Settings,
    Shield,
    Plus,
    Trash2,
    Copy,
    ExternalLink,
    ChevronLeft,
} from "lucide-react";

// ── Tabs ──────────────────────────────────────────────────────────────────────
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

// ── Form state ────────────────────────────────────────────────────────────────
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

const EventDetailPage = () => {
    const { id } = useParams() as { id: string };
    const navigate = useNavigate();

    const [event, setEvent] = useState<Event | null>(null);
    const [isLoading, setLoading] = useState(true);
    const [isSaving, setSaving] = useState(false);
    const [isDeleting, setDeleting] = useState(false);
    const [searchParams, setSearchParams] = useSearchParams();
    const activeTab = (searchParams.get("tab") as Tab) ?? "general";
    const setActiveTab = (tab: Tab) => setSearchParams({ tab });
    const [form, setForm] = useState<FormState | null>(null);

    useEffect(() => {
        getEvent(Number(id))
            .then((event) => {
                setEvent(event);
                setForm({
                    eventName: event.eventName,
                    description: event.description ?? "",
                    eventStart: toDateTimeLocal(event.eventStart),
                    eventEnd: toDateTimeLocal(event.eventEnd),
                    slotDurationMinutes:
                        event.availabilityRulesDTO.slotDurationMinutes,
                    bufferMinutes:
                        event.availabilityRulesDTO.bufferMinutes ?? 0,
                    minimumNoticeHours:
                        event.availabilityRulesDTO.minimumNoticeHours ?? 0,
                    maximumAdvanceDays:
                        event.availabilityRulesDTO.maximumAdvanceDays ?? 30,
                    maxCapacity: event.availabilityRulesDTO.maxCapacity ?? 1,
                    maxSlotsPerUser:
                        event.availabilityRulesDTO.maxSlotsPerUser ?? 1,
                    allowCancellations:
                        event.availabilityRulesDTO.allowCancellations ?? true,
                    isPublic: event.availabilityRulesDTO.isPublic ?? true,
                    fields:
                        event.bookingForm?.fields?.map((f) => ({
                            label: f.label,
                            fieldType: f.fieldType,
                            required: f.required,
                            displayOrder: f.displayOrder,
                        })) ?? [],
                });
            })
            .catch((error) => {
                if (axios.isAxiosError(error)) {
                    toast.error(
                        error.response?.data?.message ?? "Failed to load event",
                    );
                } else {
                    toast.error("Something went wrong");
                }
            })
            .finally(() => setLoading(false));
    }, [id]);

    // ── handleSave ────────────────────────────────────────────────────────────
    // booking-form tab  → PATCH /events/:id/booking-form  (fast, no slot regen)
    // all other tabs    → PUT   /events/:id               (full update)
    const handleSave = async () => {
        if (!form) return;
        setSaving(true);
        try {
            let updated: Event;

            if (activeTab === "booking-form") {
                updated = await updateBookingForm(
                    { fields: form.fields },
                    Number(id),
                );
            } else {
                const payload: EventRequest = {
                    eventName: form.eventName,
                    description: form.description || undefined,
                    eventStart: new Date(form.eventStart).toISOString(),
                    eventEnd: new Date(form.eventEnd).toISOString(),
                    availabilityRulesDTO: {
                        slotDurationMinutes: form.slotDurationMinutes,
                        bufferMinutes: form.bufferMinutes,
                        minimumNoticeHours: form.minimumNoticeHours,
                        maximumAdvanceDays: form.maximumAdvanceDays,
                        maxCapacity: form.maxCapacity,
                        maxSlotsPerUser: form.maxSlotsPerUser,
                        allowCancellations: form.allowCancellations,
                        isPublic: form.isPublic,
                    },
                };
                updated = await updateEvent(payload, Number(id));
            }

            setEvent(updated);
            toast.success("Saved");
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(
                    error.response?.data?.message ?? "Failed to save event",
                );
            } else {
                toast.error("Something went wrong");
            }
        } finally {
            setSaving(false);
        }
    };

    const handleDelete = async () => {
        if (!event) return;
        setDeleting(true);
        try {
            await deleteEvent(event.id);
            toast.success("Event deleted");
            navigate(-1);
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(
                    error.response?.data?.message ?? "Failed to delete event",
                );
            } else {
                toast.error("Something went wrong");
            }
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
                        fieldType: "TEXT",
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
            <div className="flex items-center justify-center h-64">
                <span className="loading loading-spinner loading-md text-primary" />
            </div>
        );

    if (!event || !form)
        return (
            <div className="flex items-center justify-center h-64 text-base-content/40 text-sm">
                Event not found.
            </div>
        );

    const bookingUrl = `${window.location.origin}/book/${event.shareableId}`;

    return (
        <div className="p-6 max-w-7xl mx-auto">
            {/* ── Header ── */}
            <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-3">
                    <button
                        className="btn btn-ghost btn-sm btn-circle"
                        onClick={() => navigate(-1)}
                    >
                        <ChevronLeft className="w-4 h-4" />
                    </button>
                    <div>
                        <h1 className="text-lg font-bold text-base-content">
                            {event.eventName}
                        </h1>
                        <p className="text-xs text-base-content/40">
                            Event settings
                        </p>
                    </div>
                </div>

                <div className="flex items-center gap-2">
                    <span className="text-xs font-medium text-base-content/60 bg-base-content/10 px-2 py-1 rounded-sm">
                        {form.isPublic ? "Public" : "Private"}
                    </span>
                    <input
                        type="checkbox"
                        className="toggle toggle-primary toggle-sm"
                        checked={form.isPublic}
                        onChange={(e) => set("isPublic", e.target.checked)}
                    />

                    <div className="w-px h-5 bg-base-300 mx-1" />

                    <button
                        className="btn btn-ghost btn-xs btn-square"
                        title="Copy booking link"
                        onClick={() => {
                            navigator.clipboard.writeText(bookingUrl);
                            toast.success("Link copied!");
                        }}
                    >
                        <Copy className="w-3.5 h-3.5" />
                    </button>

                    <a
                        href={bookingUrl}
                        target="_blank"
                        rel="noreferrer"
                        className="btn btn-ghost btn-xs btn-square"
                        title="View booking page"
                    >
                        <ExternalLink className="w-3.5 h-3.5" />
                    </a>

                    <button
                        className="btn btn-ghost btn-xs btn-square text-error hover:bg-error/10 rounded-lg"
                        title="Delete event"
                        disabled={isDeleting}
                        onClick={handleDelete}
                    >
                        {isDeleting ? (
                            <span className="loading loading-spinner loading-xs" />
                        ) : (
                            <Trash2 className="w-3.5 h-3.5" />
                        )}
                    </button>

                    <div className="w-px h-5 bg-base-300 mx-1" />

                    <button
                        className="btn btn-primary btn-sm"
                        onClick={handleSave}
                        disabled={isSaving}
                    >
                        {isSaving ? (
                            <span className="loading loading-spinner loading-xs" />
                        ) : (
                            "Save"
                        )}
                    </button>
                </div>
            </div>

            {/* ── Layout ── */}
            <div className="flex flex-col lg:flex-row gap-6">
                <div className="lg:w-48 lg:shrink-0">
                    <nav className="flex flex-row lg:flex-col gap-1 overflow-x-auto pb-1 lg:pb-0 scrollbar-hide">
                        {TABS.map((tab) => (
                            <button
                                key={tab.id}
                                onClick={() => setActiveTab(tab.id)}
                                className={`flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm font-medium transition-colors whitespace-nowrap lg:w-full cursor-pointer
                                    ${
                                        activeTab === tab.id
                                            ? "bg-base-200 text-base-content"
                                            : "text-base-content/50 hover:text-base-content hover:bg-base-200/50"
                                    }`}
                            >
                                {tab.icon}
                                {tab.label}
                            </button>
                        ))}
                    </nav>
                </div>

                <div className="flex-1 bg-base-100 border border-base-300 rounded-xl p-6">
                    {/* ── General ── */}
                    {activeTab === "general" && (
                        <div className="flex flex-col gap-5">
                            <h2 className="text-sm font-semibold text-base-content">
                                General
                            </h2>

                            <div className="flex flex-col gap-1.5">
                                <label className="text-sm font-medium">
                                    Event name
                                </label>
                                <input
                                    type="text"
                                    className="input input-bordered w-full outline-none"
                                    value={form.eventName}
                                    onChange={(e) =>
                                        set("eventName", e.target.value)
                                    }
                                />
                            </div>

                            <div className="flex flex-col gap-1.5">
                                <label className="text-sm font-medium">
                                    Description
                                </label>
                                <textarea
                                    className="textarea textarea-bordered w-full resize-none outline-none"
                                    rows={3}
                                    value={form.description}
                                    onChange={(e) =>
                                        set("description", e.target.value)
                                    }
                                    placeholder="Optional description shown to attendees"
                                />
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div className="flex flex-col gap-1.5">
                                    <label className="text-sm font-medium">
                                        Start date & time
                                    </label>
                                    <input
                                        type="datetime-local"
                                        className="input input-bordered w-full outline-none"
                                        value={form.eventStart}
                                        onChange={(e) =>
                                            set("eventStart", e.target.value)
                                        }
                                    />
                                </div>
                                <div className="flex flex-col gap-1.5">
                                    <label className="text-sm font-medium">
                                        End date & time
                                    </label>
                                    <input
                                        type="datetime-local"
                                        className="input input-bordered w-full outline-none"
                                        value={form.eventEnd}
                                        onChange={(e) =>
                                            set("eventEnd", e.target.value)
                                        }
                                    />
                                </div>
                            </div>

                            <div className="flex flex-col gap-1.5">
                                <label className="text-sm font-medium">
                                    Slot duration (minutes)
                                </label>
                                <input
                                    type="number"
                                    className="input input-bordered w-full outline-none"
                                    value={form.slotDurationMinutes}
                                    min={5}
                                    onChange={(e) =>
                                        set(
                                            "slotDurationMinutes",
                                            Number(e.target.value),
                                        )
                                    }
                                />
                            </div>
                        </div>
                    )}

                    {/* ── Limits ── */}
                    {activeTab === "limits" && (
                        <div className="flex flex-col gap-5">
                            <h2 className="text-sm font-semibold text-base-content">
                                Limits
                            </h2>

                            <div className="grid grid-cols-2 gap-4">
                                <div className="flex flex-col gap-1.5">
                                    <label className="text-sm font-medium">
                                        Buffer time (minutes)
                                    </label>
                                    <p className="text-xs text-base-content/40">
                                        Time between consecutive bookings
                                    </p>
                                    <input
                                        type="number"
                                        className="input input-bordered w-full outline-none"
                                        value={form.bufferMinutes}
                                        min={0}
                                        onChange={(e) =>
                                            set(
                                                "bufferMinutes",
                                                Number(e.target.value),
                                            )
                                        }
                                    />
                                </div>
                                <div className="flex flex-col gap-1.5">
                                    <label className="text-sm font-medium">
                                        Minimum notice (hours)
                                    </label>
                                    <p className="text-xs text-base-content/40">
                                        How far in advance must someone book
                                    </p>
                                    <input
                                        type="number"
                                        className="input input-bordered w-full outline-none"
                                        value={form.minimumNoticeHours}
                                        min={0}
                                        onChange={(e) =>
                                            set(
                                                "minimumNoticeHours",
                                                Number(e.target.value),
                                            )
                                        }
                                    />
                                </div>
                                <div className="flex flex-col gap-1.5">
                                    <label className="text-sm font-medium">
                                        Maximum advance (days)
                                    </label>
                                    <p className="text-xs text-base-content/40">
                                        How far ahead someone can book
                                    </p>
                                    <input
                                        type="number"
                                        className="input input-bordered w-full outline-none"
                                        value={form.maximumAdvanceDays}
                                        min={1}
                                        onChange={(e) =>
                                            set(
                                                "maximumAdvanceDays",
                                                Number(e.target.value),
                                            )
                                        }
                                    />
                                </div>
                                <div className="flex flex-col gap-1.5">
                                    <label className="text-sm font-medium">
                                        Max capacity per slot
                                    </label>
                                    <p className="text-xs text-base-content/40">
                                        Max attendees per time slot
                                    </p>
                                    <input
                                        type="number"
                                        className="input input-bordered w-full outline-none"
                                        value={form.maxCapacity}
                                        min={1}
                                        onChange={(e) =>
                                            set(
                                                "maxCapacity",
                                                Number(e.target.value),
                                            )
                                        }
                                    />
                                </div>
                                <div className="flex flex-col gap-1.5">
                                    <label className="text-sm font-medium">
                                        Max slots per user
                                    </label>
                                    <p className="text-xs text-base-content/40">
                                        How many slots one person can book
                                    </p>
                                    <input
                                        type="number"
                                        className="input input-bordered w-full outline-none"
                                        value={form.maxSlotsPerUser}
                                        min={1}
                                        onChange={(e) =>
                                            set(
                                                "maxSlotsPerUser",
                                                Number(e.target.value),
                                            )
                                        }
                                    />
                                </div>
                            </div>

                            <div className="flex items-center justify-between px-4 py-3 border border-base-300 rounded-xl">
                                <div>
                                    <p className="text-sm font-medium">
                                        Allow cancellations
                                    </p>
                                    <p className="text-xs text-base-content/40">
                                        Let attendees cancel their bookings
                                    </p>
                                </div>
                                <input
                                    type="checkbox"
                                    className="toggle toggle-primary toggle-sm"
                                    checked={form.allowCancellations}
                                    onChange={(e) =>
                                        set(
                                            "allowCancellations",
                                            e.target.checked,
                                        )
                                    }
                                />
                            </div>
                        </div>
                    )}

                    {/* ── Availability ── */}
                    {activeTab === "availability" && (
                        <div className="flex flex-col gap-5">
                            <h2 className="text-sm font-semibold text-base-content">
                                Availability
                            </h2>
                            <div className="flex flex-col items-center justify-center py-12 gap-3 text-center border border-dashed border-base-300 rounded-xl">
                                <Calendar className="w-8 h-8 text-base-content/20" />
                                <p className="text-sm text-base-content/40">
                                    Schedule linking coming soon
                                </p>
                                <p className="text-xs text-base-content/30">
                                    You'll be able to link this event to an
                                    availability schedule
                                </p>
                            </div>
                        </div>
                    )}

                    {/* ── Booking Form ── */}
                    {activeTab === "booking-form" && (
                        <div className="flex flex-col gap-5">
                            <div className="flex items-center justify-between">
                                <h2 className="text-sm font-semibold text-base-content">
                                    Booking Form
                                </h2>
                                <button
                                    type="button"
                                    className="btn btn-ghost btn-xs gap-1"
                                    onClick={addField}
                                >
                                    <Plus className="w-3.5 h-3.5" />
                                    Add field
                                </button>
                            </div>

                            {form.fields.length === 0 ? (
                                <div className="flex flex-col items-center justify-center py-12 gap-3 text-center border border-dashed border-base-300 rounded-xl">
                                    <FileText className="w-8 h-8 text-base-content/20" />
                                    <p className="text-sm text-base-content/40">
                                        No custom fields yet
                                    </p>
                                    <button
                                        type="button"
                                        className="btn btn-ghost btn-xs gap-1"
                                        onClick={addField}
                                    >
                                        <Plus className="w-3.5 h-3.5" />
                                        Add your first field
                                    </button>
                                </div>
                            ) : (
                                <div className="flex flex-col gap-3">
                                    {form.fields.map((field, index) => (
                                        <div
                                            key={index}
                                            className="flex items-center gap-3 p-4 border border-base-300 rounded-xl"
                                        >
                                            <div className="flex-1 grid grid-cols-2 gap-3">
                                                <div className="flex flex-col gap-1">
                                                    <label className="text-xs font-medium text-base-content/60">
                                                        Label
                                                    </label>
                                                    <input
                                                        type="text"
                                                        className="input input-bordered input-sm w-full outline-none"
                                                        value={field.label}
                                                        placeholder="e.g. Phone number"
                                                        onChange={(e) =>
                                                            updateField(index, {
                                                                label: e.target
                                                                    .value,
                                                            })
                                                        }
                                                    />
                                                </div>
                                                <div className="flex flex-col gap-1">
                                                    <label className="text-xs font-medium text-base-content/60">
                                                        Type
                                                    </label>
                                                    <select
                                                        className="select select-bordered select-sm w-full outline-none"
                                                        value={field.fieldType}
                                                        onChange={(e) =>
                                                            updateField(index, {
                                                                fieldType: e
                                                                    .target
                                                                    .value as
                                                                    | "TEXT"
                                                                    | "PHONE",
                                                            })
                                                        }
                                                    >
                                                        <option value="TEXT">
                                                            Text
                                                        </option>
                                                        <option value="PHONE">
                                                            Phone
                                                        </option>
                                                    </select>
                                                </div>
                                            </div>
                                            <div className="flex items-center gap-3 shrink-0">
                                                <label className="flex items-center gap-1.5 text-xs text-base-content/60 cursor-pointer">
                                                    <input
                                                        type="checkbox"
                                                        className="checkbox checkbox-xs"
                                                        checked={field.required}
                                                        onChange={(e) =>
                                                            updateField(index, {
                                                                required:
                                                                    e.target
                                                                        .checked,
                                                            })
                                                        }
                                                    />
                                                    Required
                                                </label>
                                                <button
                                                    type="button"
                                                    className="btn btn-ghost btn-xs text-error hover:bg-error/10 rounded-lg"
                                                    onClick={() =>
                                                        removeField(index)
                                                    }
                                                >
                                                    <Trash2 className="w-3.5 h-3.5" />
                                                </button>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    )}

                    {/* ── Advanced ── */}
                    {activeTab === "advanced" && (
                        <div className="flex flex-col gap-5">
                            <h2 className="text-sm font-semibold text-base-content">
                                Advanced
                            </h2>

                            <div className="flex flex-col gap-1.5">
                                <label className="text-sm font-medium">
                                    Booking link
                                </label>
                                <div className="flex items-center gap-2">
                                    <input
                                        type="text"
                                        className="input input-bordered w-full outline-none text-sm text-base-content/60"
                                        value={bookingUrl}
                                        readOnly
                                    />
                                    <button
                                        type="button"
                                        className="btn btn-ghost btn-sm btn-square"
                                        onClick={() => {
                                            navigator.clipboard.writeText(
                                                bookingUrl,
                                            );
                                            toast.success("Link copied!");
                                        }}
                                    >
                                        <Copy className="w-4 h-4" />
                                    </button>
                                    <a
                                        href={bookingUrl}
                                        target="_blank"
                                        rel="noreferrer"
                                        className="btn btn-ghost btn-sm btn-square"
                                    >
                                        <ExternalLink className="w-4 h-4" />
                                    </a>
                                </div>
                            </div>

                            <div className="flex flex-col items-center justify-center py-12 gap-3 text-center border border-dashed border-base-300 rounded-xl">
                                <Calendar className="w-8 h-8 text-base-content/20" />
                                <p className="text-sm text-base-content/40">
                                    Calendar sync coming soon
                                </p>
                                <p className="text-xs text-base-content/30">
                                    Connect Google Calendar or Outlook to sync
                                    bookings automatically
                                </p>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default EventDetailPage;
