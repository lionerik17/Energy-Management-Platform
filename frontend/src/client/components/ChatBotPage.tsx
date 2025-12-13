import { useState } from "react";
import styles from "../../commons/chat/ChatRoomPage.module.css";
import { useChat } from "../../commons/api/chatWebSocket.ts";

const ChatBotPage = () => {
    const receiver = "bot";
    const { messages, send, classifySender } = useChat(receiver);

    const [input, setInput] = useState("");

    const sendMessageToBot = () => {
        if (!input.trim()) return;
        send(input.trim());
        setInput("");
    };

    return (
        <div className={styles.pageWrapper}>
            <div className={styles.mainContent}>
                <h1 className={styles.chatTitle}>Bot Chat</h1>

                <div className={styles.chatBox}>
                    {messages.map((msg) => (
                        <div
                            key={`${msg.sender}-${msg.data.timestamp}`}
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
                        placeholder="Ask something..."
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && sendMessageToBot()}
                    />
                    <button className={styles.sendButton} onClick={sendMessageToBot}>
                        Send
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ChatBotPage;
