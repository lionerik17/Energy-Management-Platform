import hashlib
from config import settings

def select_replica(device_id: int) -> int:
    device_id_str = str(device_id)

    h = int(
        hashlib.sha256(device_id_str.encode("utf-8")).hexdigest(),
        16
    )

    return (h % settings.MONITORING_REPLICAS) + 1
