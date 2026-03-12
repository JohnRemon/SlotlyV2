import type { DataResponse } from "@/types/api";
import API from "../../../../lib/api";
import type {
    ConnectionStatus,
    ConnectResponse,
    ExchangeRequest,
} from "../types/Calendar";

export const GoogleCalendarApi = {
    initiateConnection: () =>
        API.get<DataResponse<ConnectResponse>>(
            "/api/v1/calendar/google/connect",
        ),

    exchangeAuthorizationCode: (payload: ExchangeRequest) =>
        API.post("/api/v1/calendar/google/exchange", payload),

    getConnectionStatus: () =>
        API.get<DataResponse<ConnectionStatus>>(
            "/api/v1/calendar/google/status",
        ),

    disconnectCalendar: () => API.delete("/api/v1/calendar/google/disconnect"),
};
