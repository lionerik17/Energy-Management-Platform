interface RechartsPayload {
    name: string;
    value: number;
    color?: string;
    dataKey?: string;
}

interface CustomTooltipProps {
    active?: boolean;
    payload?: RechartsPayload[];
    label?: string | number;
}

export const CustomTooltip = ({ active, payload, label }: CustomTooltipProps) => {
    if (!active || !payload || payload.length === 0) return null;

    return (
        <div
            style={{
                background: "rgba(15, 23, 42, 0.95)",
                padding: "10px 14px",
                borderRadius: "8px",
                border: "1px solid #475569",
                color: "white",
                fontSize: "0.9rem"
            }}
        >
            <div style={{ marginBottom: 4, opacity: 0.8 }}>{label}</div>

            {payload.map((item: RechartsPayload, idx: number) => (
                <div key={idx}>
                    <span>{item.name}: </span>
                    <strong>{item.value}</strong>
                </div>
            ))}
        </div>
    );
};
