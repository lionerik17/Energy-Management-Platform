import time
from datetime import datetime, timedelta
import json
import pika
import random
from pika.credentials import PlainCredentials
from dotenv import load_dotenv
import os

load_dotenv()

RABBITMQ_USER = os.getenv("RABBITMQ_USER", "guest")
RABBITMQ_PASS = os.getenv("RABBITMQ_PASSWORD", "guest")

EXCHANGE = "device.data.exchange"
ROUTING_KEY = "device.data.key"
QUEUE_NAME = "device.data.queue"

stop_flags = {}


def generate_consumption(hour: int, max_value: float) -> float:
    if 0 <= hour < 6:
        base = max_value * 0.15
    elif 6 <= hour < 12:
        base = max_value * 0.40
    elif 12 <= hour < 18:
        base = max_value * 0.60
    elif 18 <= hour < 22:
        base = max_value * 0.90
    else:
        base = max_value * 0.45

    small_fluct = random.uniform(-0.1, 0.1) * base

    if random.random() < 0.9:
        random_factor = random.uniform(0.5, 1.0)
        base = base * random_factor

    if random.random() < 0.25:
        value = base + small_fluct
    else:
        value = max_value + small_fluct

    return round(max(value, 0.0), 2)


def is_overconsumption(value: float, max_value: float) -> bool:
    return value >= max_value


def run_simulator(device_id: int, max_value: float):
    simulated_time = datetime(2025, 1, 1, 0, 0, 0)
    stop_flags[device_id] = False

    credentials = PlainCredentials(RABBITMQ_USER, RABBITMQ_PASS)
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host="rabbitmq", credentials=credentials)
    )
    channel = connection.channel()

    channel.exchange_declare(exchange=EXCHANGE, exchange_type="direct", durable=True)
    channel.queue_declare(queue=QUEUE_NAME, durable=True)
    channel.queue_bind(queue=QUEUE_NAME, exchange=EXCHANGE, routing_key=ROUTING_KEY)

    print(f"[Simulator] Started for device {device_id}")

    while not stop_flags[device_id]:
        hour = simulated_time.hour
        value = generate_consumption(hour, max_value)

        alert = None
        if is_overconsumption(value, max_value):
            alert = {
                "type": "OVERCONSUMPTION",
                "maxAllowed": max_value
            }

        payload = {
            "deviceId": device_id,
            "timestamp": simulated_time.isoformat(),
            "value": value,
            "alert": alert
        }

        channel.basic_publish(
            exchange=EXCHANGE,
            routing_key=ROUTING_KEY,
            body=json.dumps(payload)
        )

        print("[x] Sent", payload)

        simulated_time += timedelta(minutes=10)
        time.sleep(5)

    print(f"[Simulator] STOP for device {device_id}")
    connection.close()
