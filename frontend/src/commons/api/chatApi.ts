import { HOST } from "../hosts.ts";

export async function sendMessage(sender: string, receiver: string, message: string, senderRole: "USER" | "ADMIN") {
    const query = new URLSearchParams({ sender, receiver, message });
    const token = localStorage.getItem("token");

    if (!token) {
        console.warn("Token not found");
        return;
    }

    const endpoint =
        senderRole === "ADMIN"
            ? `${HOST.backend_cs}/send/admin?`
            : `${HOST.backend_cs}/send/user?`;

    return fetch(endpoint + query.toString(), {
        method: "POST",
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });
}