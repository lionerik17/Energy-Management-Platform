import {
    Table,
    TableHead,
    TableBody,
    TableRow,
    TableCell,
    TableContainer,
    Paper,
    IconButton,
    Collapse,
    Box,
} from "@mui/material";
import { LinkOff, ExpandLess, ExpandMore } from "@mui/icons-material";
import { useState } from "react";
import type {DeviceUserAssociation} from "../../api/associationsApi.ts";
import styles from "../../AdminPage.module.css";
import React from "react";

interface Props {
    associations: DeviceUserAssociation[];
    onUnassign: (userId: number, deviceId: number) => void;
    onUnassignAll: (userId: number) => void;
}

const AssociationsTable = ({ associations, onUnassign, onUnassignAll }: Props) => {
    const grouped = associations.reduce<Record<number, DeviceUserAssociation[]>>(
        (acc, curr) => {
            if (!acc[curr.userId]) acc[curr.userId] = [];
            acc[curr.userId].push(curr);
            return acc;
        },
        {}
    );

    const [openRows, setOpenRows] = useState<Record<number, boolean>>({});

    const toggleRow = (userId: number) =>
        setOpenRows((prev) => ({ ...prev, [userId]: !prev[userId] }));

    return (
        <TableContainer component={Paper} className={styles.tableContainer}>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell className={styles.tableHeader}>User</TableCell>
                        <TableCell className={styles.tableHeader}>Devices</TableCell>
                        <TableCell className={styles.tableHeader} align="center">
                            Actions
                        </TableCell>
                    </TableRow>
                </TableHead>

                <TableBody>
                    {Object.entries(grouped).map(([userId, devices]) => (
                        <React.Fragment key={userId}>
                            <TableRow
                                className={styles.tableRow}
                                onClick={() => toggleRow(Number(userId))}
                                sx={{ cursor: "pointer" }}
                            >
                                <TableCell className={styles.tableCell}>
                                    {devices[0]?.username ?? "Unknown user"}
                                </TableCell>
                                <TableCell className={styles.tableCell}>
                                    {devices.length} device(s)
                                </TableCell>
                                <TableCell align="center">
                                    <IconButton
                                        sx={{ color: "#facc15" }}
                                        title="Unassign all"
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            onUnassignAll(Number(userId));
                                        }}
                                    >
                                        <LinkOff />
                                    </IconButton>

                                    {openRows[Number(userId)] ? (
                                        <ExpandLess sx={{ color: "#a5b4fc" }} />
                                    ) : (
                                        <ExpandMore sx={{ color: "#a5b4fc" }} />
                                    )}
                                </TableCell>
                            </TableRow>

                            <TableRow>
                                <TableCell
                                    colSpan={3}
                                    sx={{
                                        padding: 0,
                                        borderBottom: "none",
                                        backgroundColor: "#1e293b",
                                    }}
                                >
                                    <Collapse
                                        in={openRows[Number(userId)]}
                                        timeout="auto"
                                        unmountOnExit
                                        sx={{ width: "100%" }}
                                    >
                                        <Box
                                            sx={{
                                                width: "100%",
                                                backgroundColor: "#1e293b",
                                                borderTop: "1px solid rgba(255,255,255,0.08)",
                                            }}
                                        >
                                            <Table
                                                size="small"
                                                sx={{
                                                    width: "100%",
                                                    tableLayout: "fixed",
                                                    backgroundColor: "#1e293b",
                                                }}
                                            >
                                                <TableHead>
                                                    <TableRow>
                                                        <TableCell className={styles.tableHeader}>
                                                            Device Name
                                                        </TableCell>
                                                        <TableCell className={styles.tableHeader}>
                                                            Device Serial Number
                                                        </TableCell>
                                                        <TableCell
                                                            className={styles.tableHeader}
                                                            align="center"
                                                        >
                                                            Actions
                                                        </TableCell>
                                                    </TableRow>
                                                </TableHead>

                                                <TableBody>
                                                    {devices.map((d) => (
                                                        <TableRow
                                                            key={d.deviceId}
                                                            className={styles.innerTableRow}
                                                        >
                                                            <TableCell className={styles.tableCell}>
                                                                {d.deviceName}
                                                            </TableCell>
                                                            <TableCell className={styles.tableCell}>
                                                                {d.deviceSerialNumber}
                                                            </TableCell>
                                                            <TableCell align="center">
                                                                <IconButton
                                                                    onClick={() =>
                                                                        onUnassign(d.userId, d.deviceId)
                                                                    }
                                                                    sx={{ color: "#fca5a5" }}
                                                                >
                                                                    <LinkOff />
                                                                </IconButton>
                                                            </TableCell>
                                                        </TableRow>
                                                    ))}
                                                </TableBody>
                                            </Table>
                                        </Box>
                                    </Collapse>
                                </TableCell>
                            </TableRow>
                        </React.Fragment>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
};

export default AssociationsTable;
