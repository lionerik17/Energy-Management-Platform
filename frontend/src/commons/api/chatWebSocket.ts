import { useEffect, useRef, useState } from "react";
import { getUserDetails } from "../../client/api/userApi";
import { sendMessage } from "./chatApi";
import type { ChatMessage, UserDTO } from "../types";

export function useChat(receiver: string) {
    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [user, setUser] = useState<UserDTO | null>(null);
    const wsRef = useRef<WebSocket | null>(null);

    useEffect(() => {
        let ws: WebSocket;

        async function init() {
            const u = await getUserDetails();
            if (!u) return;

            setUser(u);

            ws = new WebSocket(
                `ws://localhost/api/ws/chat?userId=${u.username}`
            );

            ws.onmessage = (event: MessageEvent) => {
                const msg: ChatMessage = JSON.parse(event.data);

                if (msg.sender === u.username) return;

                const isBotToUser =
                    msg.sender === "bot" && msg.receiver === u.username;

                if (!isBotToUser) return;

                setMessages(prev =>
                    [...prev, msg]
                );
            };

            wsRef.current = ws;
        }

        init();

        return () => {
            ws?.close();
        };
    }, [receiver]);

    const send = async (text: string) => {
        if (!user || !text.trim()) return;

        const userMsg: ChatMessage = {
            sender: user.username,
            receiver,
            data: {
                message: text,
                timestamp: new Date().toISOString(),
            },
        };

        setMessages(prev => [...prev, userMsg]);

        setTimeout(async () => {
            await sendMessage(
                user.username,
                receiver,
                text,
                user.role as "CLIENT" | "ADMIN"
            );
        }, 500);
    };

    const classifySender = (sender: string): "me" | "bot" | "other" => {
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
