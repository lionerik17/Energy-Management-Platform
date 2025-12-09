import type {RegisterFormData} from "../../register/components/RegisterForm.tsx";

export interface AuthValidationResult {
    isValid: boolean;
    errors: Record<string, string>;
}

export function validateAuth(data: RegisterFormData): AuthValidationResult {
    const errors: Record<string, string> = {};

    if (!data.username || data.username.trim().length === 0) {
        errors.username = "Username cannot be empty.";
    } else if (data.username.trim().length < 6) {
        errors.username = "Username must be at least 6 characters long.";
    }

    if (!data.password || data.password.trim().length === 0) {
        errors.password = "Password cannot be empty.";
    } else if (data.password.trim().length < 6) {
        errors.password = "Password must be at least 6 characters long.";
    }

    if (data.age == null || data.age < 0) {
        errors.age = "Age must be a positive number.";
    } else if (!Number.isInteger(data.age)) {
        errors.age = "Age must be an integer.";
    } else if (data.age < 18 || data.age > 100) {
        errors.age = "Age must be between 18 and 100";
    } else if (data.age > Number.MAX_SAFE_INTEGER || data.age > 100) {
        errors.age = "Age is too large";
    }

    return {
        isValid: Object.keys(errors).length === 0,
        errors,
    };
}
