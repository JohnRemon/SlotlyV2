import API from "@/lib/api";

export const UserApi = {
    updateFirstName: (id: number, firstName: string) =>
        API.patch(`/api/v1/users/${id}/first-name`, null, {
            params: {
                firstName,
            },
        }),

    updateLastName: (id: number, lastName: string) =>
        API.patch(`/api/v1/users/${id}/last-name`, null, {
            params: {
                lastName,
            },
        }),

    updateTimeZone: (id: number, timeZone: string) =>
        API.patch(`/api/v1/users/${id}/timezone`, null, {
            params: {
                timeZone,
            },
        }),
};
