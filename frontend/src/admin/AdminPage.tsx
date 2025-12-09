import { useEffect } from "react";
import { useNavigate, Routes, Route, Navigate } from "react-router-dom";
import { authorize } from "../commons/api/authorize.ts";
import styles from "./AdminPage.module.css"
import UsersView from "./components/UsersView.tsx";
import DevicesView from "./components/DevicesView.tsx";
import AssociationsView from "./components/AssociationsView.tsx";
import AdminSidebar from "./components/AdminSidebar.tsx";
import SelectClientPage from "./components/SelectClientPage.tsx";
import ChatRoomPage from "../commons/chat/ChatRoomPage.tsx";

const AdminPage = () => {
    const navigate = useNavigate();

    useEffect(() => {
        authorize().then((isAdmin) => {
            if (!isAdmin) {
                console.warn("Access denied – not an admin");
                localStorage.clear();
                navigate("/login");
            } else {
                console.log("Admin authorized!");
            }
        });
    }, [navigate]);

    return (
        <div className={styles.pageWrapper}>
            <AdminSidebar />
            <main className={styles.mainContent}>
                <Routes>
                    <Route index element={<Navigate to="users" replace />} />
                    <Route path="users" element={<UsersView />} />
                    <Route path="devices" element={<DevicesView />} />
                    <Route path="associations" element={<AssociationsView />} />
                    <Route path="chat/select-user" element={<SelectClientPage />} />
                    <Route path="chat/room/:receiverId" element={<ChatRoomPage />} />
                </Routes>
            </main>
        </div>
    );
};

export default AdminPage;
