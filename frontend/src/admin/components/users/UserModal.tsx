import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
} from "@mui/material";
import styles from "../../AdminPage.module.css";
import type {UserDTO} from "../../../commons/types.ts";

interface UserModalProps {
    open: boolean;
    onClose: () => void;
    onSave: (user: UserDTO, isCreating: boolean) => void;
    isCreating: boolean;
    user: UserDTO | null;
    setUser: (u: UserDTO) => void;
}

const UserModal = ({ open, onClose, onSave, isCreating, user, setUser }: UserModalProps) => {
    if (!user) return null;

    const handleSave = () => onSave(user, isCreating);

    return (
        <Dialog
            open={open}
            onClose={onClose}
            slotProps={{
                paper: { className: styles.dialogCard },
            }}
        >
            <DialogTitle className={styles.dialogTitle}>
                {isCreating ? "Add User" : "Edit User"}
            </DialogTitle>

            <DialogContent className={styles.dialogBody}>

                <label className={styles.label}>Username</label>
                <input
                    type="text"
                    value={user.username}
                    onChange={(e) => setUser({ ...user, username: e.target.value })}
                    className={styles.input}
                    placeholder="Enter username"
                />

                <label className={styles.label}>Password</label>
                <input
                    type="password"
                    value={user.password ?? ""}
                    onChange={(e) => setUser({ ...user, password: e.target.value })}
                    className={styles.input}
                    placeholder="Enter password"
                />

                <label className={styles.label}>Role</label>
                <select
                    value={user.role}
                    disabled
                    className={styles.input}
                >
                    <option value="CLIENT">CLIENT</option>
                </select>

                <label className={styles.label}>Age</label>
                <input
                    type="number"
                    min="1"
                    step="any"
                    value={user.age && user.age  > 0 ? user.age : 1}
                    onChange={(e) => {
                        const value = Math.max(1, Number(e.target.value));
                        setUser({...user, age: value});
                    }}
                    onKeyDown={(e) => {
                        if (e.key === "-" || e.key === "e") e.preventDefault();
                    }}
                    className={styles.input}
                    placeholder="Enter age"
                />
            </DialogContent>

            <DialogActions className={styles.dialogActions}>
                <button onClick={onClose} className={styles.cancelButton}>Cancel</button>
                <button onClick={handleSave} className={styles.button}>Save</button>
            </DialogActions>
        </Dialog>
    );
};

export default UserModal;
