import { Client } from "@stomp/stompjs";

export interface LiveUpdate {
    id: number;
    deviceId: number;
    hour: string;
    totalConsumption: number;
}

let stompClient: Client | null = null;

export function connectToLive(
    deviceId: number,
    onMessage: (msg: LiveUpdate) => void
): Promise<void> {
    return new Promise((resolve, reject) => {
        const token = localStorage.getItem("token");

        stompClient = new Client({
            reconnectDelay: 5000,
            webSocketFactory: () => new WebSocket("ws://localhost/api/ws/monitor"),

            connectHeaders: {
                Authorization: `Bearer ${token}`,
            },

            onConnect: () => {
                console.log("WS Connected!");
                stompClient?.subscribe(`/topic/device/${deviceId}`, (msg) =>
                    onMessage(JSON.parse(msg.body))
                );
                resolve();
            },

            onStompError: (frame) => {
                console.error("STOMP error:", frame.headers["message"]);
                reject(frame);
            },

            debug: () => {},
        });

        stompClient.activate();
    });
}

export function disconnectLive() {
    if (stompClient) {
        stompClient.deactivate();
        stompClient = null;
    }
}
