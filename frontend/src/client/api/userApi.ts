import { HOST } from "../../commons/hosts.ts";
import { userDetailsCache } from "../cache/UserDetailsCache.ts"
import type {UserDTO} from "../../commons/types.ts";

export interface UserDetails {
    id: number;
    username: string;
    role: string;
    age: number;
}

const endpoint = {
    details: "/details",
    admins: "/admins",
};

let cachedAdmins: UserDTO[] | null = null;

export async function getUserDetails(): Promise<UserDetails | null> {
    const token = localStorage.getItem("token");
    if (!token) {
        console.warn("No token!");
        return null;
    }

    try {
        const response = await fetch(HOST.backend_user + endpoint.details, {
            headers: { Authorization: `Bearer ${token}` },
        });

        if (response.ok) {
            const user = (await response.json()) as UserDetails;
            userDetailsCache.set(user);
            return user;
        }

        console.warn("User service offline — using memory user cache.");
        return userDetailsCache.get();
    } catch (error) {
        console.error("Error fetching user details:", error);
        return userDetailsCache.get();
    }
}

export async function getAllAdmins(): Promise<UserDTO[] | null> {
    const token = localStorage.getItem("token");
    if (!token) {
        console.warn("No token!");
        return null;
    }

    try {
        const response = await fetch(HOST.backend_user + endpoint.admins, {
            headers: { Authorization: `Bearer ${token}` },
        });

        if (response.ok) {
            const users: UserDTO[] = await response.json();
            cachedAdmins = users;
            return users;
        }

        console.warn("User service offline — using memory user cache.");
        return cachedAdmins;
    } catch (error) {
        console.error("Error fetching user details:", error);
        return cachedAdmins;
    }
}