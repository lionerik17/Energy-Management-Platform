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
import type {DeviceDTO} from "../../../commons/types.ts";

interface DevicesTableProps {
    devices: DeviceDTO[];
    onAdd: () => void;
    onEdit: (device: DeviceDTO) => void;
    onDelete: (id: number) => void;
}

const DevicesTable = ({ devices, onAdd, onEdit, onDelete }: DevicesTableProps) => {
    return (
        <>
            <Button
                variant="contained"
                startIcon={<Add />}
                className={styles.addButton}
                onClick={onAdd}
            >
                Add Device
            </Button>

            <TableContainer component={Paper} className={styles.tableContainer}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell className={styles.tableHeader}>Name</TableCell>
                            <TableCell className={styles.tableHeader}>Serial Number</TableCell>
                            <TableCell className={styles.tableHeader}>Max Consumption</TableCell>
                            <TableCell className={styles.tableHeader} align="center">
                                Actions
                            </TableCell>
                        </TableRow>
                    </TableHead>

                    <TableBody>
                        {devices.map((d) => (
                            <TableRow key={d.id} className={styles.tableRow}>
                                <TableCell className={styles.tableCell}>{d.name}</TableCell>
                                <TableCell className={styles.tableCell}>{d.serialNumber}</TableCell>
                                <TableCell className={styles.tableCell}>{d.maxConsumptionValue}</TableCell>
                                <TableCell align="center">
                                    <IconButton
                                        onClick={() => onEdit(d)}
                                        sx={{ color: "#93c5fd" }}
                                    >
                                        <Edit />
                                    </IconButton>
                                    <IconButton
                                        onClick={() => onDelete(d.id!)}
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

export default DevicesTable;
