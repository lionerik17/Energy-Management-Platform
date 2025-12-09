import { useEffect, useState } from "react";
import { getAllClients } from "../api/usersAdminApi";
import { useNavigate } from "react-router-dom";
import styles from "../../commons/chat/SelectPage.module.css";
import type {UserDTO} from "../../commons/types.ts";

const SelectClientPage = () => {
    const [users, setUsers] = useState<UserDTO[]>([]);
    const navigate = useNavigate();

    useEffect(() => {
        async function load() {
            const list = await getAllClients();
            if (list) setUsers(list);
        }
        load();
    }, []);

    return (
        <div className={styles.pageWrapper}>
            <h1 className={styles.title}>Select Client</h1>

            <div className={styles.container}>
                <ul className={styles.list}>
                    {users.map((user) => (
                        <li
                            key={user.id}
                            className={styles.card}
                            onClick={() => navigate(`/admin/chat/room/${user.username}`)}
                        >
                            <p className={styles.name}>{user.username}</p>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default SelectClientPage;
