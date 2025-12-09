import { useEffect, useState, useRef } from "react";
import {
    startSimulation,
    stopSimulation,
    getHistoryForDay,
    type HourlyHistory
} from "../api/monitoringApi";

import { type Device, getUserDevices } from "../api/deviceApi";

import {
    connectToLive,
    disconnectLive,
    type LiveUpdate
} from "../api/monitoringWebSocket";

import Grid from "@mui/material/Grid";
import {
    Card,
    CardContent,
    Typography,
    Button,
    FormControl,
    InputLabel,
    MenuItem,
    Select,
    TextField
} from "@mui/material";

import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer
} from "recharts";

import styles from "../ClientPage.module.css";
import {CustomTooltip} from "./CustomTooltip.tsx";

const MonitoringView = () => {
    const [devices, setDevices] = useState<Device[]>([]);
    const [selected, setSelected] = useState<number | null>(null);

    const [selectedDay, setSelectedDay] = useState<string>(() => {
        return new Date().toISOString().split("T")[0];
    });
    const selectedDayRef = useRef(selectedDay);

    const [maxValue, setMaxValue] = useState<number>(0);

    const [dayHistory, setDayHistory] = useState<HourlyHistory[]>([]);
    const [liveData, setLiveData] = useState<LiveUpdate[]>([]);

    const [simRunning, setSimRunning] = useState(false);
    const wsConnectedRef = useRef(false);

    useEffect(() => {
        getUserDevices().then((devs) => {
            if (devs) setDevices(devs);
        });
    }, []);

    useEffect(() => {
        selectedDayRef.current = selectedDay;
    }, [selectedDay]);

    const handleDeviceSelect = async (deviceId: number) => {
        setSelected(deviceId);
        setSimRunning(false);
        setLiveData([]);
        setDayHistory([]);

        const dev = devices.find((d) => d.id === deviceId);
        if (dev) setMaxValue(dev.maxConsumptionValue);

        await connectToDevice(deviceId);
    };

    const connectToDevice = async (deviceId: number) => {
        if (wsConnectedRef.current) disconnectLive();

        await connectToLive(deviceId, (msg: LiveUpdate) => {
            setLiveData(prev => [...prev, msg].slice(-100));

            const msgDay = msg.hour.split("T")[0];

            if (msgDay === selectedDayRef.current) {
                setDayHistory(prev => {
                    const hour = msg.hour;
                    const existingIndex = prev.findIndex(h => h.hour === hour);

                    if (existingIndex !== -1) {
                        const updated = [...prev];
                        updated[existingIndex] = msg;
                        return updated;
                    }

                    return [...prev, msg].sort((a, b) =>
                        a.hour.localeCompare(b.hour)
                    );
                });
            }
        });

        wsConnectedRef.current = true;
    };

    const toggleSimulation = async () => {
        if (!selected) return;

        if (!simRunning) {
            const ok = await startSimulation(selected, maxValue);
            if (ok) setSimRunning(true);
            alert(ok ? "Simulation started!" : "Failed to start");
        } else {
            const ok = await stopSimulation(selected);
            if (ok) setSimRunning(false);
            alert(ok ? "Simulation stopped!" : "Failed to stop");
        }
    };

    const handleLoadDay = async () => {
        if (!selected) return;
        const history = await getHistoryForDay(selected, selectedDay);
        if (history) setDayHistory(history);
    };

    useEffect(() => () => disconnectLive(), []);

    return (
        <div className={styles.mainContent}>
            <Typography variant="h4" sx={{ fontWeight: 700, mb: 3 }}>
                Device Monitoring
            </Typography>

            <Grid container spacing={4}>

                {/* LEFT PANEL */}
                <Grid size={{ xs: 12, md: 4 }}>
                    <Card sx={{ background: "rgba(255,255,255,0.06)" }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Select Device
                            </Typography>

                            <FormControl fullWidth margin="normal">
                                <InputLabel>Select device</InputLabel>
                                <Select
                                    value={selected ?? ""}
                                    label="Select device"
                                    onChange={(e) => {
                                        const id = Number(e.target.value);
                                        if (!isNaN(id)) handleDeviceSelect(id);
                                    }}
                                >
                                    <MenuItem value="">
                                        <em>None</em>
                                    </MenuItem>
                                    {devices.map((d) => (
                                        <MenuItem key={d.id} value={d.id}>
                                            {d.name} (#{d.id})
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>

                            {/* TOGGLE BUTTON */}
                            <Button
                                variant="contained"
                                fullWidth
                                sx={{ mt: 2 }}
                                color={simRunning ? "error" : "primary"}
                                onClick={toggleSimulation}
                                disabled={!selected}
                            >
                                {simRunning ? "Stop Simulation" : "Start Simulation"}
                            </Button>

                            <Typography variant="h6" sx={{ mt: 4 }}>
                                Select Day
                            </Typography>

                            <TextField
                                fullWidth
                                type="date"
                                value={selectedDay}
                                onChange={(e) => setSelectedDay(e.target.value)}
                                sx={{ mt: 2 }}
                                slotProps={{ inputLabel: { shrink: true } }}
                            />

                            <Button
                                variant="outlined"
                                fullWidth
                                sx={{ mt: 2 }}
                                onClick={handleLoadDay}
                                disabled={!selected}
                            >
                                Load Day History
                            </Button>
                        </CardContent>
                    </Card>
                </Grid>

                {/* RIGHT PANEL */}
                <Grid size={{ xs: 12, md: 8 }}>

                    {/* LIVE CHART */}
                    <Card sx={{ background: "rgba(255,255,255,0.06)", mb: 3, maxHeight: 400 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Live Energy Usage
                            </Typography>

                            {liveData.length === 0 ? (
                                <Typography color="gray">Waiting for live data…</Typography>
                            ) : (
                                <ResponsiveContainer width="100%" height={300}>
                                    <LineChart data={liveData}>
                                        <CartesianGrid strokeDasharray="3 3" />
                                        <XAxis dataKey="hour" tickFormatter={(v) => v.slice(11, 16)} />
                                        <YAxis />
                                        <Tooltip content={<CustomTooltip />} />
                                        <Line
                                            type="monotone"
                                            dataKey="totalConsumption"
                                            stroke="#ff9800"
                                            strokeWidth={2}
                                            dot={false}
                                        />
                                    </LineChart>
                                </ResponsiveContainer>
                            )}
                        </CardContent>
                    </Card>

                    {/* DAILY CHART */}
                    <Card sx={{ background: "rgba(255,255,255,0.06)", mb: 3, maxHeight: 400 }}>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Daily Energy Chart
                            </Typography>

                            {dayHistory.length === 0 ? (
                                <Typography color="gray">
                                    Select a day to view energy usage.
                                </Typography>
                            ) : (
                                <ResponsiveContainer width="100%" height={300}>
                                    <LineChart data={dayHistory}>
                                        <CartesianGrid strokeDasharray="3 3" />
                                        <XAxis dataKey="hour" tickFormatter={(v) => v.slice(11, 16)} />
                                        <YAxis />
                                        <Tooltip content={<CustomTooltip />} />
                                        <Line
                                            type="monotone"
                                            dataKey="totalConsumption"
                                            stroke="#90caf9"
                                            strokeWidth={2}
                                            dot={false}
                                        />
                                    </LineChart>
                                </ResponsiveContainer>
                            )}
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </div>
    );
};

export default MonitoringView;
