import type {UserDTO} from "../../commons/types.ts";

export interface ValidationResult {
    isValid: boolean;
    errors: Record<string, string>;
}

export function validateUser(user: UserDTO, existingUsers: UserDTO[]): ValidationResult {
    const errors: Record<string, string> = {};

    if (!user.username || user.username.trim().length === 0) {
        errors.username = "Username cannot be empty.";
    } else if (user.username.trim().length < 6) {
        errors.username = "Username must be at least 6 characters long.";
    }

    const duplicate = existingUsers.some(
        (d) =>
            d.username.trim().toLowerCase() === user.username.trim().toLowerCase() &&
            d.id !== user.id
    );

    if (duplicate) {
        errors.username = "Another user already uses this username.";
    }

    if (user.age == null || user.age < 0) {
        errors.age = "Age must be a positive number.";
    } else if (!Number.isInteger(user.age)) {
        errors.age = "Age must be an integer.";
    } else if (user.age < 18 || user.age > 100) {
            errors.age = "Age must be between 18 and 100";
    } else if (user.age > Number.MAX_SAFE_INTEGER) {
        errors.age = "Age is too large";
    }

    if (!user.password || user.password.trim().length === 0) {
        errors.password = "Password cannot be empty.";
    } else if (user.password.trim().length < 6) {
        errors.password = "Password must be at least 6 characters long.";
    }

    return {
        isValid: Object.keys(errors).length === 0,
        errors,
    };
}
