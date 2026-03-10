export interface DataResponse<T> {
    data: T;
}

export interface PagedResponse<T> {
    data: T[];
    meta: PageMetadata;
}

export interface PageMetadata {
    number: number;
    size = 10;
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
}

export interface ErrorResponse {
    message: string;
    code: string;
    status: number;
    path: string;
}
