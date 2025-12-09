import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
} from "@mui/material";
import styles from "../../AdminPage.module.css";
import type {DeviceDTO} from "../../../commons/types.ts";

interface DeviceModalProps {
    open: boolean;
    onClose: () => void;
    onSave: (device: DeviceDTO, isCreating: boolean) => void;
    isCreating: boolean;
    device: DeviceDTO | null;
    setDevice: (d: DeviceDTO) => void;
}

const DeviceModal = ({ open, onClose, onSave, isCreating, device, setDevice }: DeviceModalProps) => {
    if (!device) return null;

    const handleSave = () => onSave(device, isCreating);

    return (
        <Dialog
            open={open}
            onClose={onClose}
            slotProps={{
                paper: {
                    className: styles.dialogCard,
                },
            }}
        >
            <DialogTitle className={styles.dialogTitle}>
                {isCreating ? "Add Device" : "Edit Device"}
            </DialogTitle>

            <DialogContent className={styles.dialogBody}>
                <label className={styles.label}>Name</label>
                <input
                    type="text"
                    value={device.name}
                    onChange={(e) => setDevice({...device, name: e.target.value})}
                    className={styles.input}
                    placeholder="Enter device name"
                />

                <label className={styles.label}>Serial Number</label>
                <input
                    type="text"
                    value={device.serialNumber}
                    onChange={(e) => setDevice({ ...device, serialNumber: e.target.value })}
                    className={styles.input}
                    placeholder="Enter serial number"
                />

                <label className={styles.label}>Max Consumption</label>
                <input
                    type="number"
                    value={device.maxConsumptionValue && device.maxConsumptionValue > 0 ? device.maxConsumptionValue : 1}
                    onChange={(e) => {
                        const value = Math.max(1, Number(e.target.value));
                        setDevice({...device, maxConsumptionValue: value});
                    }}
                    className={styles.input}
                    placeholder="Enter max consumption"
                    min="1"
                    step="any"
                    onKeyDown={(e) => {
                        if (e.key === "-" || e.key === "e") e.preventDefault();
                    }}
                />
            </DialogContent>

            <DialogActions className={styles.dialogActions}>
                <button onClick={onClose} className={styles.cancelButton}>
                    Cancel
                </button>
                <button onClick={handleSave} className={styles.button}>
                    Save
                </button>
            </DialogActions>
        </Dialog>
    );
};

export default DeviceModal;
