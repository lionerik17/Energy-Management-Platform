import { HOST } from "../../commons/hosts";
import { stopSimulator } from "../../commons/simulator"
import type {DeviceDTO} from "../../commons/types.ts";

let cachedDevicesAdmin: DeviceDTO[] | null = null;

export async function getAllDevices(): Promise<DeviceDTO[] | null> {
    const token = localStorage.getItem("token");

    try {
        const res = await fetch(HOST.backend_device, {
            headers: { Authorization: `Bearer ${token}` },
        });

        if (res.ok) {
            const devices: DeviceDTO[] = await res.json();

            cachedDevicesAdmin = devices;

            console.log(`Cached (memory only) ${devices.length} devices`);
            return devices;
        }

        throw new Error("Service unavailable");
    } catch (err) {
        console.error("Failed to fetch devices:", err);

        if (cachedDevicesAdmin) {
            console.log(
                `Loaded ${cachedDevicesAdmin.length} cached devices (API fallback)`
            );
            return cachedDevicesAdmin;
        }

        return null;
    }
}

export async function createDevice(device: DeviceDTO): Promise<DeviceDTO | null> {
    const token = localStorage.getItem("token");

    const res = await fetch(HOST.backend_device, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(device),
    });

    if (!res.ok) return null;

    const created = await res.json();

    if (cachedDevicesAdmin) {
        cachedDevicesAdmin = [...cachedDevicesAdmin, created];
    }

    return created;
}

export async function updateDevice(id: number, device: DeviceDTO): Promise<DeviceDTO | null> {
    const token = localStorage.getItem("token");

    const res = await fetch(HOST.backend_device + `/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(device),
    });

    if (!res.ok) return null;

    const updated = await res.json();

    if (cachedDevicesAdmin) {
        cachedDevicesAdmin = cachedDevicesAdmin.map(d =>
            d.id === updated.id ? updated : d
        );
    }

    const stopped = await stopSimulator(device.id!);

    if (!stopped) {
        console.warn("Device updated successfully, but failed to stop simulation")
    }

    return updated;
}

export async function deleteDevice(id: number): Promise<boolean> {
    const token = localStorage.getItem("token");

    const res = await fetch(HOST.backend_device + `/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
    });

    if (!res.ok) return false;

    if (cachedDevicesAdmin) {
        cachedDevicesAdmin = cachedDevicesAdmin.filter(d => d.id !== id);
    }

    const stopped = await stopSimulator(id);

    if (!stopped) {
        console.warn("Device deleted successfully, but failed to stop simulation")
    }

    return true;
}
