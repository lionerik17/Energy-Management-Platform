import { useEffect, useState } from "react";
import {
    getAllDevices,
    deleteDevice,
    updateDevice,
    createDevice,
} from "../api/devicesAdminApi.ts";
import { Typography, CircularProgress, Box } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { authorize } from "../../commons/api/authorize.ts";
import DevicesTable from "./devices/DevicesTable.tsx";
import DeviceModal from "./devices/DeviceModal.tsx";
import styles from "../AdminPage.module.css";
import { validateDevice } from "../validators/deviceValidator.ts";
import type {DeviceDTO} from "../../commons/types.ts";

const DevicesView = () => {
    const [devices, setDevices] = useState<DeviceDTO[]>([]);
    const [loading, setLoading] = useState(true);
    const [modalOpen, setModalOpen] = useState(false);
    const [editingDevice, setEditingDevice] = useState<DeviceDTO | null>(null);
    const [isCreating, setIsCreating] = useState(false);
    const [deleting, setDeleting] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        getAllDevices()
            .then((devices) => {
                if (Array.isArray(devices)) {
                    setDevices(devices);
                } else {
                    setDevices([]);
                }
            })
            .finally(() => setLoading(false));
    }, []);

    useEffect(() => {
        authorize().then((isAdmin) => {
            if (!isAdmin) {
                console.warn("Access denied!");
                localStorage.clear();
                navigate("/login");
            }
        });
    }, [navigate]);

    const handleAdd = () => {
        setEditingDevice({ name: "", serialNumber: "", maxConsumptionValue: 1 });
        setIsCreating(true);
        setModalOpen(true);
    };

    const handleEdit = (device: DeviceDTO) => {
        setEditingDevice(device);
        setIsCreating(false);
        setModalOpen(true);
    };

    const handleClose = () => {
        setModalOpen(false);
        setEditingDevice(null);
        setIsCreating(false);
    };

    const handleSave = async (device: DeviceDTO, creating: boolean) => {
        const validation = validateDevice(device, devices);
        if (!validation.isValid) {
            const msg = Object.entries(validation.errors)
                .map(([field, error]) => `${field}: ${error}`)
                .join("\n");
            alert(`Validation failed:\n${msg}`);
            return;
        }

        try {
            const result = creating
                ? await createDevice(device)
                : await updateDevice(device.id!, device);

            if (!result) {
                alert("Operation failed!");
                return;
            }

            setDevices((prev) =>
                creating
                    ? [...prev, result]
                    : prev.map((d) => (d.id === result.id ? result : d))
            );

            handleClose();
        } catch (err) {
            console.error("Save failed:", err);
            alert("Unexpected error during save.");
        }
    };

    const handleDelete = async (id: number) => {
        if (!confirm("Delete this device?")) return;

        setDeleting(true);

        try {
            const success = await deleteDevice(id);

            if (!success) {
                alert("Delete device operation failed!");
                return;
            }

            setDevices((prev) => prev.filter((d) => d.id !== id));
        } catch (err) {
            console.error("Delete failed:", err);
            alert("Unexpected error during delete.");
        } finally {
            setDeleting(false);
        }
    };

    if (loading)
        return (
            <Box sx={{ display: "flex", justifyContent: "center", height: "100%" }}>
                <CircularProgress color="primary" />
            </Box>
        );

    return (
        <div className={styles.mainContent}>
            <Typography variant="h5" sx={{ fontWeight: 700, marginBottom: 2 }}>
                Manage Devices
            </Typography>

            <DevicesTable
                devices={devices}
                onAdd={handleAdd}
                onEdit={handleEdit}
                onDelete={handleDelete}
            />

            <DeviceModal
                open={modalOpen}
                onClose={handleClose}
                onSave={handleSave}
                isCreating={isCreating}
                device={editingDevice}
                setDevice={(d) => setEditingDevice(d)}
            />

            {deleting && (
                <Box
                    sx={{
                        position: "absolute",
                        inset: 0,
                        display: "flex",
                        justifyContent: "center",
                        alignItems: "center",
                        backgroundColor: "rgba(0,0,0,0.5)",
                        zIndex: 9999,
                    }}
                >
                    <CircularProgress color="secondary" />
                </Box>
            )}
        </div>
    );
};

export default DevicesView;
