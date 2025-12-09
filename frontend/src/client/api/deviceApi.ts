import { HOST } from "../../commons/hosts";
import {deviceCache} from "../cache/DeviceCache.ts"

export interface Device {
    id: number;
    serialNumber: string;
    name: string;
    maxConsumptionValue: number;
}

const endpoint = {
    details: "-users/details",
};

export async function getUserDevices(): Promise<Device[] | null> {
    const token = localStorage.getItem("token");
    if (!token) {
        console.warn("No token — returning cached memory devices.");
        return deviceCache.get();
    }

    try {
        const response = await fetch(HOST.backend_device + endpoint.details, {
            headers: { Authorization: `Bearer ${token}` },
        });

        if (response.ok) {
            const devices = (await response.json()) as Device[];
            deviceCache.set(devices);
            return devices;
        }

        console.warn("Device service offline — using memory cache.");
        return deviceCache.get();
    } catch (err) {
        console.error("Error fetching devices:", err);
        return deviceCache.get();
    }
}
