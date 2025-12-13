export interface AuthResponse {
    accessToken: string;
    username: string;
    role: string;
    expiration: number;
}

export interface UserDTO {
    id?: number;
    username: string;
    password?: string;
    role: string;
    age?: number;
}

export interface DeviceDTO {
    id?: number;
    name: string;
    serialNumber: string;
    maxConsumptionValue: number;
}

export interface ChatMessage {
    sender: string;
    receiver: string;
    data: {
        message: string;
        timestamp: string;
    };
}