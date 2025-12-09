import {
    Table,
    TableHead,
    TableBody,
    TableRow,
    TableCell,
    TableContainer,
    Paper,
    IconButton,
    Button,
} from "@mui/material";
import { Edit, Delete, Add } from "@mui/icons-material";
import styles from "../../AdminPage.module.css";
import type {UserDTO} from "../../../commons/types.ts";

interface UsersTableProps {
    users: UserDTO[];
    onAdd: () => void;
    onEdit: (user: UserDTO) => void;
    onDelete: (id: number) => void;
}

const UsersTable = ({ users, onAdd, onEdit, onDelete }: UsersTableProps) => {
    return (
        <>
            <Button
                variant="contained"
                startIcon={<Add />}
                className={styles.addButton}
                onClick={onAdd}
            >
                Add User
            </Button>

            <TableContainer component={Paper} className={styles.tableContainer}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell className={styles.tableHeader}>Username</TableCell>
                            <TableCell className={styles.tableHeader}>Role</TableCell>
                            <TableCell className={styles.tableHeader}>Age</TableCell>
                            <TableCell className={styles.tableHeader} align="center">
                                Actions
                            </TableCell>
                        </TableRow>
                    </TableHead>

                    <TableBody>
                        {users.map((u) => (
                            <TableRow key={u.id} className={styles.tableRow}>
                                <TableCell className={styles.tableCell}>{u.username}</TableCell>
                                <TableCell className={styles.tableCell}>{u.role}</TableCell>
                                <TableCell className={styles.tableCell}>{u.age}</TableCell>
                                <TableCell align="center">
                                    <IconButton
                                        onClick={() => onEdit(u)}
                                        sx={{ color: "#93c5fd" }}
                                    >
                                        <Edit />
                                    </IconButton>
                                    <IconButton
                                        onClick={() => onDelete(u.id!)}
                                        sx={{ color: "#fca5a5" }}
                                    >
                                        <Delete />
                                    </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </>
    );
};

export default UsersTable;
