import { HOST } from "../hosts.ts";

export async function sendMessage(sender: string, receiver: string, message: string, senderRole: "CLIENT" | "ADMIN") {
    const query = new URLSearchParams({ sender, receiver, message });
    const token = localStorage.getItem("token");

    if (!token) {
        console.warn("Token not found");
        return;
    }

    let url = "";

    if (receiver === "bot") {
        url = `${HOST.backend_cs}/send/bot?`;
    }
    else if (senderRole === "ADMIN") {
        url = `${HOST.backend_cs}/send/admin?`;
    } else if (senderRole === "CLIENT") {
        url = `${HOST.backend_cs}/send/user?`;
    }

    return fetch(url + query.toString(), {
        method: "POST",
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });
}