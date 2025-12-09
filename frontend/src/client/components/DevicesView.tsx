import { useEffect, useState } from "react";
import { getUserDevices, type Device } from "../api/deviceApi";
import styles from "../ClientPage.module.css";

const DevicesView = () => {
    const [devices, setDevices] = useState<Device[] | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getUserDevices().then((data) => {
            setDevices(data);
            setLoading(false);
        });
    }, []);

    if (loading) {
        return <p className={styles.loadingText}>Loading devices...</p>;
    }

    if (!devices || devices.length === 0) {
        return <p className={styles.errorText}>No devices found.</p>;
    }

    return (
        <div className={styles.devicesContainer}>
            <h2 className={styles.deviceTitle}>My Devices</h2>
            <ul className={styles.deviceList}>
                {devices.map((device) => (
                    <li key={device.id} className={styles.deviceCard}>
                        <h3 className={styles.deviceName}>{device.name}</h3>
                        <p className={styles.deviceInfo}>
                            <strong>Serial Number:</strong> {device.serialNumber}
                        </p>
                        <p className={styles.deviceInfo}>
                            <strong>Max Consumption:</strong> {device.maxConsumptionValue}
                        </p>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default DevicesView;
