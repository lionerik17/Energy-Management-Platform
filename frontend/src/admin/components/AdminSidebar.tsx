import { useNavigate } from "react-router-dom";
import styles from "../AdminPage.module.css";
import { authorize } from "../../commons/api/authorize.ts";

const AdminSidebar = () => {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.clear();
        navigate("/login");
    };

    const handlePageChange = async (path: string) => {
        const isAdmin = await authorize();
        if (!isAdmin) {
            alert("Access denied!");
            navigate("/login");
            return;
        }
        navigate(path);
    };

    return (
        <aside className={styles.sidebar}>
            <div className={styles.logo}>Admin Panel</div>

            <nav className={styles.nav}>
                <button
                    className={styles.navButton}
                    onClick={() => handlePageChange("/admin/users")}
                >
                    Users
                </button>
                <button
                    className={styles.navButton}
                    onClick={() => handlePageChange("/admin/devices")}
                >
                    Devices
                </button>
                <button
                    className={styles.navButton}
                    onClick={() => handlePageChange("/admin/associations")}
                >
                    Associations
                </button>
                <button
                    className={styles.navButton}
                    onClick={() => handlePageChange("/admin/chat/select-user")}
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

export default AdminSidebar;
