export interface ExchangeRequest {
    code: string;
    state: string;
}
export interface ConnectResponse {
    authorizationUrl: string;
}

export interface ConnectionStatus {
    status: boolean;
}
