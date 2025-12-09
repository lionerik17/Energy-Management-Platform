import { HOST } from "./hosts";

export async function stopSimulator(deviceId: number): Promise<boolean> {
    const token = localStorage.getItem("token");

    try {
        const res = await fetch(
            `${HOST.simulator}/device-deleted?device_id=${deviceId}`,
            {
                method: "POST",
                headers: { Authorization: `Bearer ${token}` },
            }
        );

        if (!res.ok) {
            console.warn(`[Simulator] Failed to stop for device ${deviceId}`);
            return false;
        }

        return true;

    } catch (err) {
        console.warn(`[Simulator] Offline or unreachable (${deviceId})`, err);
        return false;
    }
}

