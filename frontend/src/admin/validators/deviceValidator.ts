import type {DeviceDTO} from "../../commons/types.ts";

export interface ValidationResult {
    isValid: boolean;
    errors: Record<string, string>;
}

export function validateDevice(device: DeviceDTO, existingDevices: DeviceDTO[]): ValidationResult {
    const errors: Record<string, string> = {};

    if (!device.name || device.name.trim().length === 0) {
        errors.name = "Device name cannot be empty.";
    } else if (device.name.trim().length < 6) {
        errors.name = "Device name must be at least 6 characters long.";
    }

    if (!device.serialNumber || device.serialNumber.trim().length === 0) {
        errors.serialNumber = "Serial number cannot be empty.";
    } else if (device.serialNumber.trim().length < 6) {
        errors.serialNumber = "Serial number must be at least 6 characters long.";
    }

    const duplicate = existingDevices.some(
        (d) =>
            d.serialNumber.trim().toLowerCase() === device.serialNumber.trim().toLowerCase() &&
            d.id !== device.id
    );

    if (duplicate) {
        errors.serialNumber = "Another device already uses this serial number.";
    }

    if (device.maxConsumptionValue == null) {
        errors.maxConsumptionValue = "Max consumption value is required.";
    } else if (device.maxConsumptionValue < 0) {
        errors.maxConsumptionValue = "Max consumption must be greater than zero.";
    } else if (!Number.isInteger(device.maxConsumptionValue)) {
        errors.maxConsumptionValue = "Max consumption must be an integer.";
    } else if (device.maxConsumptionValue > Number.MAX_SAFE_INTEGER) {
        errors.maxConsumptionValue = "Max consumption is too large";
    }

    return {
        isValid: Object.keys(errors).length === 0,
        errors,
    };
}
