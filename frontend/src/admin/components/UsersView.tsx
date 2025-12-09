import { useEffect, useState } from "react";
import {
    getAllClients,
    deleteUser,
    updateUser,
    createUser,
    waitForUser,
} from "../api/usersAdminApi.ts";
import { Typography, CircularProgress, Box } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { authorize } from "../../commons/api/authorize.ts";
import UsersTable from "./users/UsersTable.tsx";
import UserModal from "./users/UserModal.tsx";
import styles from "../AdminPage.module.css";
import { validateUser } from "../validators/userValidator.ts";
import type {UserDTO} from "../../commons/types.ts";

const UsersView = () => {
    const [users, setUsers] = useState<UserDTO[]>([]);
    const [loading, setLoading] = useState(true);
    const [modalOpen, setModalOpen] = useState(false);
    const [editingUser, setEditingUser] = useState<UserDTO | null>(null);
    const [isCreating, setIsCreating] = useState(false);
    const [deleting, setDeleting] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        getAllClients()
            .then((users) => {
                if (Array.isArray(users)) {
                    setUsers(users);
                } else {
                    setUsers([]);
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
        setEditingUser({ username: "", password: "", role: "CLIENT", age: 1 });
        setIsCreating(true);
        setModalOpen(true);
    };

    const handleEdit = (user: UserDTO) => {
        setEditingUser({ ...user });
        setIsCreating(false);
        setModalOpen(true);
    };

    const handleClose = () => {
        setModalOpen(false);
        setEditingUser(null);
        setIsCreating(false);
    };

    const handleSave = async (user: UserDTO, creating: boolean) => {
        const validation = validateUser(user, users);
        if (!validation.isValid) {
            const msg = Object.entries(validation.errors)
                .map(([field, error]) => `${field}: ${error}`)
                .join("\n");
            alert(`Validation failed:\n${msg}`);
            return;
        }

        try {
            const result = creating
                ? await createUser(user)
                : await updateUser(user.id!, user);

            if (!result) {
                alert("Operation failed!");
                return;
            }

            const newUser = await waitForUser(result.id!)
            if (!newUser) {
                alert("Failed to fetch user!");
                return;
            }

            setUsers((prev) =>
                creating
                    ? [...prev, newUser]
                    : prev.map((u) => (u.id === newUser.id ? newUser : u))
            );

            handleClose();
        } catch (err) {
            console.error("Save failed:", err);
            alert("Unexpected error during save.");
        }
    };

    const handleDelete = async (id: number) => {
        if (!confirm(`Delete user?`)) return;

        setDeleting(true);

        try {
            const success = await deleteUser(id);

            if (!success) {
                alert("Failed to delete user");
                return;
            }

            setUsers((prev) => prev.filter((u) => u.id !== id));
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
                Manage Users
            </Typography>

            <UsersTable
                users={users}
                onAdd={handleAdd}
                onEdit={handleEdit}
                onDelete={handleDelete}
            />

            <UserModal
                open={modalOpen}
                onClose={handleClose}
                onSave={handleSave}
                isCreating={isCreating}
                user={editingUser}
                setUser={(u) => setEditingUser(u)}
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

export default UsersView;
