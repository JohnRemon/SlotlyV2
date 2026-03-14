import { CalendarX, Loader2Icon, Plus } from "lucide-react";
import { useState } from "react";
import { useNavigate } from "react-router";
import { Button } from "@/components/ui/button";
import ScheduleRow from "./ScheduleRow";
import CreateScheduleModal from "../components/CreateScheduleModal";
import { useSchedulesContext } from "../context/schedulesContextStore";

const SchedulesPage = () => {
    const { schedules, isLoading, remove } = useSchedulesContext();
    const [showModal, setShowModal] = useState(false);
    const navigate = useNavigate();

    return (
        <div className="mx-auto flex w-full max-w-7xl flex-col gap-6">
            <div className="flex items-center justify-between gap-4">
                <div>
                    <h1 className="text-base font-semibold tracking-[-0.01em]">
                        Schedules
                    </h1>
                    <p className="mt-1 text-xs text-muted-foreground">
                        Manage your working hours and schedules
                    </p>
                </div>
                <Button type="button" onClick={() => setShowModal(true)}>
                    <Plus className="size-4" />
                    New schedule
                </Button>
            </div>

            {isLoading ? (
                <div className="flex justify-center py-16">
                    <Loader2Icon className="size-5 animate-spin text-muted-foreground" />
                </div>
            ) : schedules.length === 0 ? (
                <div className="rounded-2xl border border-dashed bg-card/40 p-10 shadow-sm ring-1 ring-foreground/5 supports-backdrop-filter:backdrop-blur-sm">
                    <div className="mx-auto flex max-w-sm flex-col items-center justify-center gap-4 text-center">
                        <div className="flex size-14 items-center justify-center rounded-2xl bg-muted/40 ring-1 ring-foreground/10">
                            <CalendarX className="size-6 text-muted-foreground" />
                        </div>
                        <div>
                            <p className="text-sm font-semibold tracking-[-0.01em]">
                                No schedules yet
                            </p>
                            <p className="mt-1 text-xs text-muted-foreground">
                                Create your first schedule to get started
                            </p>
                        </div>
                        <Button
                            type="button"
                            onClick={() => setShowModal(true)}
                        >
                            <Plus className="size-4" />
                            New schedule
                        </Button>
                    </div>
                </div>
            ) : (
                <div className="flex flex-col gap-2">
                    {schedules.map((schedule) => (
                        <ScheduleRow
                            key={schedule.id}
                            schedule={schedule}
                            onDelete={remove}
                        />
                    ))}
                </div>
            )}

            {showModal && (
                <CreateScheduleModal
                    onClose={() => setShowModal(false)}
                    onSuccess={(id) => navigate(`/schedules/${id}`)}
                />
            )}
        </div>
    );
};

export default SchedulesPage;
