export interface DataResponse<T> {
    data: T;
}

export interface PagedResponse<T> {
    content: T[];
    page: PageMetadata;
}

export interface PageMetadata {
    number: number;
    size: number;
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
