import ClientSidebar from "./components/ClientSidebar.tsx";
import styles from "./ClientPage.module.css";
import {useEffect} from "react";
import {verifyToken} from "../commons/api/verifyToken.ts";
import {Navigate, Route, Routes, useNavigate} from "react-router-dom";
import DevicesView from "./components/DevicesView.tsx";
import ProfileView from "./components/ProfileView.tsx";
import MonitoringView from "./components/MonitoringView.tsx";
import SelectAdminPage from "./components/SelectAdminPage.tsx";
import ChatRoomPage from "../commons/chat/ChatRoomPage.tsx";
import ChatBotPage from "./components/ChatBotPage.tsx";

const ClientPage = () => {
    const navigate = useNavigate();

    useEffect(() => {
        verifyToken().then(isValid => {
            if (isValid) {
                console.log("Authenticated!");
            } else {
                console.warn("Invalid token!");
                localStorage.clear();
                navigate("/login");
            }
        });
    }, [navigate]);

    return (
        <div className={styles.pageWrapper}>
            <ClientSidebar />
            <main className={styles.mainContent}>
                <Routes>
                    <Route index element={<Navigate to="profile" replace/>}/>
                    <Route path="profile" element={<ProfileView/>}/>
                    <Route path="devices" element={<DevicesView/>}/>
                    <Route path="monitor" element={<MonitoringView/>}/>
                    <Route path="chat/select-admin" element={<SelectAdminPage />} />
                    <Route path="chat/room/:receiverId" element={<ChatRoomPage />} />
                    <Route path="/chat/bot" element={<ChatBotPage />} />
                </Routes>
            </main>
        </div>
    );
};

export default ClientPage;
