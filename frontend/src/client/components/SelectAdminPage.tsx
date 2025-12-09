import { useEffect, useState } from "react";
import { getAllAdmins } from "../api/userApi.ts";
import { useNavigate } from "react-router-dom";
import styles from "../../commons/chat/SelectPage.module.css";
import type {UserDTO} from "../../commons/types.ts";

const SelectAdminPage = () => {
    const [admins, setAdmins] = useState<UserDTO[]>([]);
    const navigate = useNavigate();

    useEffect(() => {
        async function load() {
            const list = await getAllAdmins();
            if (list) setAdmins(list);
        }
        load();
    }, []);

    return (
        <div className={styles.pageWrapper}>
            <h1 className={styles.title}>Choose Admin</h1>

            <div className={styles.container}>

                <ul className={styles.list}>
                    {admins.map((admin) => (
                        <li
                            key={admin.id}
                            className={styles.card}
                            onClick={() => navigate(`/client/chat/room/${admin.username}`)}
                        >
                            <p className={styles.name}>{admin.username}</p>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default SelectAdminPage;
