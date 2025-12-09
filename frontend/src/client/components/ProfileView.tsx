import { useEffect, useState } from "react";
import { getUserDetails, type UserDetails } from "../api/userApi";
import styles from "../ClientPage.module.css";

const ProfileView = () => {
    const [user, setUser] = useState<UserDetails | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getUserDetails().then((data) => {
            setUser(data);
            setLoading(false);
        });
    }, []);

    if (loading) {
        return <p className={styles.loadingText}>Loading user details...</p>;
    }

    if (!user) {
        return <p className={styles.errorText}>Failed to load user details.</p>;
    }

    return (
        <div className={styles.profileContainer}>
            <h2 className={styles.profileTitle}>Profile</h2>

            <div className={styles.profileCard}>
                <div className={styles.profileRow}>
                    <span className={styles.label}>Username:</span>
                    <span className={styles.value}>{user.username}</span>
                </div>
                <div className={styles.profileRow}>
                    <span className={styles.label}>Role:</span>
                    <span className={styles.value}>{user.role}</span>
                </div>
                <div className={styles.profileRow}>
                    <span className={styles.label}>Age:</span>
                    <span className={styles.value}>{user.age}</span>
                </div>
            </div>
        </div>
    );
};

export default ProfileView;
