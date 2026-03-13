import { Loader2Icon, Plus } from "lucide-react";
import { useState } from "react";
import ScheduleRow from "./ScheduleRow";
import CreateScheduleModal from "../components/CreateScheduleModal";
import { useNavigate } from "react-router";
import { useSchedulesContext } from "../context/schedulesContextStore";

import { Button } from "@/components/ui/button";

const SchedulesPage = () => {
    const { schedules, isLoading, remove } = useSchedulesContext();
    const [showModal, setShowModal] = useState(false);
    const navigate = useNavigate();

    if (isLoading)
        return (
            <div className="flex items-center justify-center h-64">
                <Loader2Icon className="size-5 animate-spin text-muted-foreground" />
            </div>
        );

    return (
        <div className="mx-auto flex w-full max-w-7xl flex-col gap-6 px-4 py-8">
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

            <div className="flex flex-col gap-2">
                {schedules.map((schedule) => (
                    <ScheduleRow
                        key={schedule.id}
                        schedule={schedule}
                        onDelete={remove}
                    />
                ))}
            </div>

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
