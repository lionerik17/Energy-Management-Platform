import { HOST } from "../../commons/hosts.ts";
import type {DeviceDTO, UserDTO} from "../../commons/types.ts";


export interface AssociationResponse {
    user: { id: number; username: string };
    devices: {
        id: number;
        name: string;
        serialNumber: string;
        maxConsumptionValue: number;
    }[];
}

export interface DeviceUserAssociation {
    userId: number;
    username: string;
    deviceId: number;
    deviceName: string;
    deviceSerialNumber: string;
}

let cachedAssociationsAdmin: DeviceUserAssociation[] | null = null;

const endpoint = {
    devices_users: "-users",
    assign: "/assign",
    unassign: "/unassign",
    unassignAll: "/unassign-all",
};

export async function getAllAssociations(): Promise<DeviceUserAssociation[] | null> {
    const token = localStorage.getItem("token");

    try {
        const res = await fetch(HOST.backend_device + endpoint.devices_users, {
            headers: { Authorization: `Bearer ${token}` },
        });

        if (!res.ok) throw new Error("Device–User service unavailable");

        const data: AssociationResponse[] = await res.json();

        const associations: DeviceUserAssociation[] = data.flatMap(entry =>
            entry.devices.map(d => ({
                userId: entry.user.id,
                username: entry.user.username,
                deviceId: d.id,
                deviceName: d.name,
                deviceSerialNumber: d.serialNumber
            }))
        );

        cachedAssociationsAdmin = associations;

        console.log(`Cached (memory) ${associations.length} associations`);
        return associations;

    } catch (err) {
        console.warn("Failed to fetch associations:", err);

        if (cachedAssociationsAdmin) {
            console.log(
                `Loaded ${cachedAssociationsAdmin.length} cached associations (fallback)`
            );
            return cachedAssociationsAdmin;
        }

        return null;
    }
}

export async function assignDeviceToUser(
    user: UserDTO,
    device: DeviceDTO
): Promise<boolean> {

    const token = localStorage.getItem("token");

    try {
        const res = await fetch(
            HOST.backend_device + endpoint.devices_users + endpoint.assign + `/${user.id}/${device.id}`,
            {
                method: "POST",
                headers: { Authorization: `Bearer ${token}` },
            }
        );

        if (!res.ok) return false;

        const newAssoc: DeviceUserAssociation = {
            userId: user.id!,
            username: user.username,
            deviceId: device.id!,
            deviceName: device.name,
            deviceSerialNumber: device.serialNumber,
        };

        if (!cachedAssociationsAdmin) cachedAssociationsAdmin = [];

        const exists = cachedAssociationsAdmin.some(
            a => a.userId === user.id && a.deviceId === device.id
        );

        if (!exists) cachedAssociationsAdmin.push(newAssoc);

        return true;

    } catch (err) {
        console.error("assignDeviceToUser failed:", err);
        return false;
    }
}

export async function unassignDeviceFromUser(userId: number, deviceId: number): Promise<boolean> {
    const token = localStorage.getItem("token");

    const res = await fetch(
        HOST.backend_device + endpoint.devices_users + endpoint.unassign + `/${userId}/${deviceId}`,
        {
            method: "DELETE",
            headers: { Authorization: `Bearer ${token}` },
        }
    );

    if (!res.ok) return false;

    if (cachedAssociationsAdmin) {
        cachedAssociationsAdmin = cachedAssociationsAdmin.filter(
            a => !(a.userId === userId && a.deviceId === deviceId)
        );
    }

    return true;
}

export async function unassignAllDevices(userId: number): Promise<boolean> {
    const token = localStorage.getItem("token");

    const res = await fetch(
        HOST.backend_device + endpoint.devices_users + endpoint.unassignAll + `/${userId}`,
        {
            method: "DELETE",
            headers: { Authorization: `Bearer ${token}` },
        }
    );

    if (!res.ok) return false;

    if (cachedAssociationsAdmin) {
        cachedAssociationsAdmin = cachedAssociationsAdmin.filter(
            a => a.userId !== userId
        );
    }

    return true;
}
