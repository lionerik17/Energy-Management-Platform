import { useEffect, useRef, useState } from "react";
import { getUserDetails } from "../../client/api/userApi";
import { sendMessage } from "./chatApi.ts";
import type { ChatMessage, UserDTO } from "../types";

export function useChat(receiver: string) {
    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [user, setUser] = useState<UserDTO | null>(null);
    const wsRef = useRef<WebSocket | null>(null);

    useEffect(() => {
        async function init() {
            const u = await getUserDetails();
            if (!u) return;

            setUser(u);

            const ws = new WebSocket(`ws://localhost/api/ws/chat?userId=${u.username}`);

            ws.onopen = () => {
                console.log("[WS] Connected");
            };

            ws.onerror = () => {
                console.error("[WS] Error");
            };

            ws.onmessage = (event: MessageEvent) => {
                const msg: ChatMessage = JSON.parse(event.data);
                setMessages((prev) => [...prev, msg]);
            };

            wsRef.current = ws;
        }

        init();
    }, []);

    const send = async (text: string) => {
        if (!user || !text.trim()) return;

        const newMsg: ChatMessage = {
            sender: user.username,
            receiver,
            data: { message: text },
        };

        await sendMessage(user.username, receiver, text, user.role as "USER" | "ADMIN");

        setMessages(prev => [...prev, newMsg]);
    };

    const classifySender = (sender: string): "me" | "other" | "bot" => {
        if (!user) return "other";
        if (sender === user.username) return "me";
        if (sender === "bot") return "bot";
        return "other";
    };

    return {
        user,
        messages,
        send,
        classifySender,
    };
}
