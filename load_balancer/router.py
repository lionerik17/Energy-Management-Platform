import hashlib
from config import settings

def select_replica(device_id: str) -> int:
    h = int(hashlib.sha256(device_id.encode()).hexdigest(), 16)
    return (h % settings.MONITORING_REPLICAS) + 1
