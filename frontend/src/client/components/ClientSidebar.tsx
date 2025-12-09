import { useNavigate } from "react-router-dom";
import styles from "../ClientPage.module.css";
import {verifyToken} from "../../commons/api/verifyToken.ts";

const ClientSidebar = () => {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.clear();
        navigate("/login");
    };

    const handlePageChange = async (path: string) => {
        const validToken = await verifyToken();

        if (!validToken) {
            alert('Access denied!');
            navigate("/login");
            return;
        }

        navigate(path)
    };

    return (
        <aside className={styles.sidebar}>
            <div className={styles.logo}>Client Panel</div>

            <nav className={styles.nav}>
                <button
                    className={styles.navButton}
                    onClick={() => handlePageChange("/client/profile")}
                >
                    Profile
                </button>
                <button
                    className={styles.navButton}
                    onClick={() => handlePageChange("/client/devices")}
                >
                    Devices
                </button>
                <button
                    className={styles.navButton}
                    onClick={() => handlePageChange("/client/monitor")}
                >
                    Monitor
                </button>
                <button
                    className={styles.navButton}
                    onClick={() => handlePageChange("/client/chat/select-admin")}
                >
                    Chat
                </button>
            </nav>

            <div className={styles.logoutSection}>
                <button className={styles.logoutButton} onClick={handleLogout}>
                    Logout
                </button>
            </div>
        </aside>
    );
};

export default ClientSidebar;
