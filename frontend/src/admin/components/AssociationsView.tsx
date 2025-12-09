import { useEffect, useState } from "react";
import {
    getAllAssociations,
    assignDeviceToUser,
    unassignDeviceFromUser,
    unassignAllDevices,
    type DeviceUserAssociation,
} from "../api/associationsApi.ts";
import { getAllClients } from "../api/usersAdminApi.ts";
import { getAllDevices } from "../api/devicesAdminApi.ts";
import { Typography, Button, CircularProgress, Box } from "@mui/material";
import { AddLink } from "@mui/icons-material";
import AssociationsTable from "./associations/AssociationsTable.tsx";
import AssociationModal from "./associations/AssociationModal.tsx";
import styles from "../AdminPage.module.css";
import { authorize } from "../../commons/api/authorize.ts";
import { useNavigate } from "react-router-dom";
import type {DeviceDTO, UserDTO} from "../../commons/types.ts";

const AssociationsView = () => {
    const [associations, setAssociations] = useState<DeviceUserAssociation[]>([]);
    const [users, setUsers] = useState<UserDTO[]>([]);
    const [devices, setDevices] = useState<DeviceDTO[]>([]);
    const [loading, setLoading] = useState(true);
    const [modalOpen, setModalOpen] = useState(false);
    const [deleting, setDeleting] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        authorize().then((isAdmin) => {
            if (!isAdmin) {
                console.warn("Access denied – not an admin");
                localStorage.clear();
                navigate("/login");
            }
        });
    }, [navigate]);

    useEffect(() => {
        Promise.all([getAllAssociations(), getAllClients(), getAllDevices()]).then(
            ([assocData, userData, deviceData]) => {
                if (assocData) setAssociations(assocData);
                if (userData) setUsers(userData);
                if (deviceData) setDevices(deviceData);

                setLoading(false);
            }
        );
    }, []);

    const handleAssign = async (userId: number, deviceId: number) => {
        const user = users.find((u) => u.id === userId);
        const device = devices.find((d) => d.id === deviceId);

        if (!user || !device) {
            alert("Invalid user or device selection.");
            return false;
        }

        try {
            const success = await assignDeviceToUser(user, device);

            if (!success) {
                alert("Assign operation failed!");
                return false;
            }

            const newAssoc = {
                userId: user.id!,
                username: user.username,
                deviceId: device.id!,
                deviceName: device.name,
                deviceSerialNumber: device.serialNumber,
            };

            setAssociations((prev) => {
                const exists = prev.some(
                    (a) => a.userId === newAssoc.userId && a.deviceId === newAssoc.deviceId
                );
                if (exists) return prev;
                return [...prev, newAssoc];
            });

            return true;
        } catch (err) {
            console.error("Assign failed:", err);
            alert("Unexpected error during assign.");
            return false;
        }
    };

    const handleUnassign = async (userId: number, deviceId: number) => {
        if (!confirm("Delete this association?")) return;

        setDeleting(true);

        try {
            const success = await unassignDeviceFromUser(userId, deviceId);
            if (!success) {
                alert("Operation failed!");
                return;
            }

            setAssociations((prev) =>
                prev.filter((a) => !(a.userId === userId && a.deviceId === deviceId))
            );
        } catch (err) {
            console.error("Unassign failed:", err);
            alert("Unexpected error during unassign.");
        } finally {
            setDeleting(false);
        }
    };

    const handleUnassignAll = async (userId: number) => {
        if (!confirm("Delete all associations for user?")) return;

        setDeleting(true);

        try {
            const success = await unassignAllDevices(userId);
            if (!success) {
                alert("Operation failed!");
                return;
            }

            setAssociations((prev) => prev.filter((a) => a.userId !== userId));
        } catch (err) {
            console.error("UnassignAll failed:", err);
            alert("Unexpected error during unassign all.");
        } finally {
            setDeleting(false);
        }
    };

    if (loading)
        return (
            <Box sx={{ display: "flex", justifyContent: "center", height: "100%" }}>
                <CircularProgress />
            </Box>
        );

    return (
        <div className={styles.mainContent}>
            <Typography variant="h5" sx={{ fontWeight: 700, marginBottom: 2 }}>
                Device–User Associations
            </Typography>

            <Button
                variant="contained"
                startIcon={<AddLink />}
                className={styles.addButton}
                onClick={() => setModalOpen(true)}
            >
                Assign Device
            </Button>

            <AssociationsTable
                associations={associations}
                onUnassign={handleUnassign}
                onUnassignAll={handleUnassignAll}
            />

            <AssociationModal
                open={modalOpen}
                onClose={() => setModalOpen(false)}
                users={users.map((u) => ({
                    id: u.id!,
                    username: u.username,
                }))}
                devices={devices.map((d) => ({
                    id: d.id!,
                    name: d.name,
                }))}
                associations={associations}
                onAssign={async (userId, deviceId) => {
                    const success = await handleAssign(userId, deviceId);
                    if (success) setModalOpen(false);
                }}
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

export default AssociationsView;
