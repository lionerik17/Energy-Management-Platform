import type { Device } from "../api/deviceApi.ts";

class DeviceCache {
    private devices: Device[] | null = null;

    set(devs: Device[]) {
        this.devices = structuredClone(devs);
    }

    get(): Device[] | null {
        return this.devices ? structuredClone(this.devices) : null;
    }

    clear() {
        this.devices = null;
    }
}

export const deviceCache = new DeviceCache();
