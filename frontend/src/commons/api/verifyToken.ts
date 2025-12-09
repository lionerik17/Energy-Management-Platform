import {HOST} from "../hosts.ts";

const endpoint = {
    verify: "/verify",
};

export async function verifyToken(): Promise<boolean> {
    const token = localStorage.getItem("token");
    if (!token) {
        console.error("No token found in localStorage");
        return false;
    }

    try {
        const response = await fetch(HOST.backend_auth + endpoint.verify, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        });

        return response.ok;
    } catch (err) {
        console.error("Token verification failed:", err);
        return false;
    }
}