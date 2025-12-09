import { useState } from "react";
import { useParams } from "react-router-dom";
import styles from "./ChatRoomPage.module.css";
import {useChat} from "../api/chatWebSocket.ts";

const ChatRoomPage = () => {
    const { receiverId } = useParams();
    const receiver = receiverId!;

    const { messages, send, classifySender } = useChat(receiver);
    const [input, setInput] = useState("");

    const sendMessage = () => {
        if (!input.trim()) return;
        send(input.trim());
        setInput("");
    };

    return (
        <div className={styles.pageWrapper}>
            <div className={styles.mainContent}>
                <h1 className={styles.chatTitle}>Chat with {receiver}</h1>

                <div className={styles.chatBox}>
                    {messages.map((msg, index) => (
                        <div
                            key={index}
                            className={`${styles.bubble} ${
                                classifySender(msg.sender) === "me"
                                    ? styles.meBubble
                                    : styles.otherBubble
                            }`}
                        >
                            <p className={styles.sender}>{msg.sender}</p>
                            <p>{msg.data.message}</p>
                        </div>
                    ))}
                </div>

                <div className={styles.inputRow}>
                    <input
                        className={styles.chatInput}
                        placeholder="Type your message..."
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && sendMessage()}
                    />
                    <button className={styles.sendButton} onClick={sendMessage}>
                        Send
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ChatRoomPage;
