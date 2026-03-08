import API from "../../../../lib/api";

export const getConnectionStatus = async (): Promise<boolean> => {
    const res = await API.get("/api/v1/calendar/google/status");
    return res.data.data.connected;
};

export const initiateConnection = async (): Promise<string> => {
    const res = await API.get("/api/v1/calendar/google/connect");
    return res.data.data.authorizationUrl;
};

export const exchangeAuthorizationCode = async (
    code: string,
    state: string,
): Promise<void> => {
    await API.post("/api/v1/calendar/google/exchange", { code, state });
};

export const disconnectCalendar = async (): Promise<void> => {
    await API.delete("/api/v1/calendar/google/disconnect");
};
