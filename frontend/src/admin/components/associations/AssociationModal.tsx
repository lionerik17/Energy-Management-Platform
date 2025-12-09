import { useEffect, useState } from "react";
import { Dialog, DialogTitle, DialogContent, DialogActions } from "@mui/material";
import styles from "../../AdminPage.module.css";

interface Props {
    open: boolean;
    onClose: () => void;

    users: { id: number; username: string }[];
    devices: { id: number; name: string }[];
    associations?: { userId: number; deviceId: number }[];

    onAssign: (userId: number, deviceId: number) => void;
}

const AssociationModal = ({
                              open,
                              onClose,
                              users,
                              devices,
                              associations = [],
                              onAssign,
                          }: Props) => {

    const [selectedUserId, setSelectedUserId] = useState<number | null>(null);
    const [selectedDeviceId, setSelectedDeviceId] = useState<number | null>(null);
    const [availableDevices, setAvailableDevices] = useState<
        { id: number; name: string }[]
    >([]);

    useEffect(() => {
        if (!open) {
            setSelectedUserId(null);
            setSelectedDeviceId(null);
            setAvailableDevices(devices);
        }
    }, [open, devices]);

    useEffect(() => {
        if (!open) return;

        if (!selectedUserId) {
            setAvailableDevices(devices);
            return;
        }

        const assignedIds = associations
            .filter((a) => a.userId === selectedUserId)
            .map((a) => a.deviceId);

        const filtered = devices.filter((d) => !assignedIds.includes(d.id));
        setAvailableDevices(filtered);

    }, [selectedUserId, associations, devices, open]);

    const handleSubmit = () => {
        if (!selectedUserId || !selectedDeviceId) {
            alert("Select both user and device.");
            return;
        }
        onAssign(selectedUserId, selectedDeviceId);
    };

    return (
        <Dialog
            open={open}
            onClose={onClose}
            slotProps={{
                paper: { className: styles.dialogCard },
            }}
        >
            <DialogTitle className={styles.dialogTitle}>
                Assign Device to User
            </DialogTitle>

            <DialogContent className={styles.dialogBody}>
                <label className={styles.label}>User</label>
                <select
                    className={styles.input}
                    value={selectedUserId ?? ""}
                    onChange={(e) => {
                        setSelectedUserId(Number(e.target.value));
                        setSelectedDeviceId(null);
                    }}
                >
                    <option value="">Select User</option>
                    {users.map((u) => (
                        <option key={u.id} value={u.id}>
                            {u.username}
                        </option>
                    ))}
                </select>

                <label className={styles.label}>Device</label>
                <select
                    className={styles.input}
                    value={selectedDeviceId ?? ""}
                    onChange={(e) => setSelectedDeviceId(Number(e.target.value))}
                    disabled={!selectedUserId}
                >
                    <option value="">Select Device</option>

                    {availableDevices.length > 0 ? (
                        availableDevices.map((d) => (
                            <option key={d.id} value={d.id}>
                                {d.name}
                            </option>
                        ))
                    ) : (
                        <option disabled>No available devices</option>
                    )}
                </select>
            </DialogContent>

            <DialogActions className={styles.dialogActions}>
                <button onClick={onClose} className={styles.cancelButton}>
                    Cancel
                </button>
                <button onClick={handleSubmit} className={styles.button}>
                    Assign
                </button>
            </DialogActions>
        </Dialog>
    );
};

export default AssociationModal;
