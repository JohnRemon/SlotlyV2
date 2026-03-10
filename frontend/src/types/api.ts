export interface DataResponse<T> {
    data: T;
}

export interface PagedResponse<T> {
    data: T[];
    meta: {
        currentPage: number;
        totalPages: number;
        totalElements: number;
        pageSize: number;
    };
}

export interface ErrorResponse {
    message: string;
    code: string;
    status: number;
    path: string;
}
