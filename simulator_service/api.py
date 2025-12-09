from fastapi import FastAPI
import threading
from simulator import run_simulator, stop_flags

app = FastAPI()

threads = {}


@app.post("/simulator/start")
def start(device_id: int, max_value: float):

    if device_id in threads and threads[device_id].is_alive():
        return {"status": "already running"}

    t = threading.Thread(
        target=run_simulator,
        args=(device_id, max_value),
        daemon=True
    )
    threads[device_id] = t
    t.start()

    return {"status": "started"}


@app.post("/simulator/stop")
def stop(device_id: int):
    if device_id not in threads:
        return {"status": "not running"}

    stop_flags[device_id] = True
    return {"status": "stopping"}
