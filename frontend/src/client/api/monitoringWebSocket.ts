export interface LiveUpdate {
    deviceId: number;
    hour: string;
    totalConsumption: number;
}

export interface MonitorMessage {
    eventType: "MONITOR_ALERT";
    deviceId: number;
    value: number;
    maxAllowed: number;
    timestamp: string;
}

let ws: WebSocket | null = null;

export function connectToLive(
    userId: number,
    deviceId: number,
    onUpdate: (msg: LiveUpdate) => void,
    onAlert: (msg: MonitorMessage) => void
): Promise<void> {
    return new Promise((resolve, reject) => {
        ws = new WebSocket(`ws://localhost/api/ws/monitor?userId=${userId}`);

        ws.onopen = () => {
            console.log(`[WS] Connected. Subscribing to device ${deviceId}…`);

            ws!.send(JSON.stringify({ subscribe: deviceId }));

            resolve();
        };

        ws.onerror = (err) => {
            console.error("[WS] Error:", err);
            reject(err);
        };

        ws.onmessage = (event) => {
            const raw = JSON.parse(event.data);

            if (raw.eventType === "MONITOR_ALERT") {
                onAlert(raw);
                return;
            }

            if (raw.eventType === "MONITOR_UPDATE") {
                onUpdate({
                    deviceId: raw.deviceId,
                    hour: raw.hour,
                    totalConsumption: raw.totalConsumption,
                });
            }
        };

        ws.onclose = () => {
            console.log("[WS] Connection closed.");
        };
    });
}

export function disconnectLive() {
    if (ws) {
        console.log("[WS] Closing connection…");
        ws.close();
        ws = null;
    }
}
