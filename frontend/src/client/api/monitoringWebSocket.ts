export interface LiveUpdate {
    deviceId: number;
    hour: string;
    totalConsumption: number;
}

let ws: WebSocket | null = null;

export function connectToLive(
    deviceId: number,
    onMessage: (msg: LiveUpdate) => void
): Promise<void> {
    return new Promise((resolve, reject) => {
        ws = new WebSocket("ws://localhost/api/ws/monitor");

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
            try {
                if (!event.data) return;

                const raw = JSON.parse(event.data);

                const msg: LiveUpdate = {
                    deviceId: raw.deviceId,
                    hour: raw.hour,
                    totalConsumption: raw.totalConsumption,
                };

                if (!msg.deviceId || !msg.hour) {
                    console.warn("[WS] Ignoring invalid payload:", raw);
                    return;
                }

                onMessage(msg);

            } catch (e) {
                console.error("[WS] Failed to parse message:", e, event.data);
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
