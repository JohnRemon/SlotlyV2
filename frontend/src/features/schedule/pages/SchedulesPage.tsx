import { Plus } from "lucide-react";
import { useState } from "react";
import ScheduleRow from "./ScheduleRow";
import CreateScheduleModal from "../components/CreateScheduleModal";
import { useNavigate } from "react-router";
import { useSchedulesContext } from "../context/schedulesContextStore";

const SchedulesPage = () => {
    const { schedules, isLoading, remove } = useSchedulesContext();
    const [showModal, setShowModal] = useState(false);
    const navigate = useNavigate();

    if (isLoading)
        return (
            <div className="flex items-center justify-center h-64">
                <span className="loading loading-spinner loading-md text-primary" />
            </div>
        );

    return (
        <div className="p-6 max-w-7xl mx-auto">
            <div className="flex items-center justify-between mb-6">
                <div>
                    <h1 className="text-lg font-bold text-base-content">
                        Availability
                    </h1>
                    <p className="text-xs text-base-content/40 mt-0.5">
                        Manage your working hours and schedules
                    </p>
                </div>
                <button
                    type="button"
                    className="btn btn-primary btn-sm gap-1.5"
                    onClick={() => setShowModal(true)}
                >
                    <Plus className="w-3.5 h-3.5" />
                    New availability
                </button>
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
