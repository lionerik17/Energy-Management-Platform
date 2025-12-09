import { HOST } from "../../commons/hosts";

export interface HourlyHistory {
    hour: string;
    deviceId: number;
    totalConsumption: number;
}

export async function startSimulation(deviceId: number, maxValue: number) {
    const token = localStorage.getItem("token");

    const res = await fetch(
        `${HOST.simulator}/start?device_id=${deviceId}&max_value=${maxValue}`,
        {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    return res.ok;
}

export async function stopSimulation(deviceId: number) {
    const token = localStorage.getItem("token");

    const res = await fetch(
        `${HOST.simulator}/stop?device_id=${deviceId}`,
        {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    return res.ok;
}

export async function getHistory(deviceId: number) {
    const token = localStorage.getItem("token");

    const res = await fetch(
        `${HOST.backend_monitor}/${deviceId}`,
        {
            headers: { Authorization: `Bearer ${token}` },
        }
    );

    return res.ok ? await res.json() : [];
}

export async function getHistoryForDay(deviceId: number, date: string) {
    const token = localStorage.getItem("token");

    const res = await fetch(
        `${HOST.backend_monitor}/day/${deviceId}?date=${date}`,
        {
            headers: { Authorization: `Bearer ${token}` },
        }
    );

    return res.ok ? await res.json() : [];
}
