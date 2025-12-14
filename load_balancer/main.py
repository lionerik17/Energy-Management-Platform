import json
import threading
import time
import pika
from fastapi import FastAPI

from config import settings
from router import select_replica

app = FastAPI(title="Load Balancer Service")

INGEST_EXCHANGE = "monitor.ingest.exchange"

DATA_EXCHANGE = "device.data.exchange"
DATA_ROUTING_KEY = "device.data.key"


def connect_with_retry():
    delay = 2
    max_delay = 30

    while True:
        try:
            print("[LB] Connecting to RabbitMQ...")
            connection = pika.BlockingConnection(
                pika.URLParameters(settings.RABBITMQ_URL)
            )
            channel = connection.channel()

            channel.exchange_declare(
                exchange=INGEST_EXCHANGE,
                exchange_type="direct",
                durable=True
            )

            channel.queue_declare(
                queue=settings.DEVICE_QUEUE,
                durable=True
            )

            channel.exchange_declare(
                exchange=DATA_EXCHANGE,
                exchange_type="direct",
                durable=True
            )

            channel.queue_bind(
                exchange=DATA_EXCHANGE,
                queue=settings.DEVICE_QUEUE,
                routing_key=DATA_ROUTING_KEY
            )

            for i in range(1, settings.MONITORING_REPLICAS + 1):
                channel.queue_declare(
                    queue=f"ingest.queue.{i}",
                    durable=True
                )
                channel.queue_bind(
                    exchange=INGEST_EXCHANGE,
                    queue=f"ingest.queue.{i}",
                    routing_key=f"ingest.queue.{i}"
                )

            channel.basic_qos(prefetch_count=1)

            print("[LB] Connected to RabbitMQ")
            return connection, channel

        except pika.exceptions.AMQPConnectionError:
            print(f"[LB] RabbitMQ not ready, retrying in {delay}s...")
            time.sleep(delay)
            delay = min(delay * 2, max_delay)


def consume():
    connection, channel = connect_with_retry()

    def callback(ch, method, properties, body):
        try:
            msg = json.loads(body)
            device_id = msg.get("deviceId")

            if not device_id:
                print("[LB] Missing deviceId, dropping message")
                ch.basic_ack(method.delivery_tag)
                return

            replica = select_replica(device_id)
            routing_key = f"ingest.queue.{replica}"

            channel.basic_publish(
                exchange=INGEST_EXCHANGE,
                routing_key=routing_key,
                body=json.dumps(msg),
                properties=pika.BasicProperties(
                    delivery_mode=2
                )
            )

            print(f"[LB] device={device_id} → replica={replica}")
            ch.basic_ack(method.delivery_tag)

        except Exception as e:
            print("[LB] Error processing message:", e)
            ch.basic_nack(method.delivery_tag, requeue=True)

    channel.basic_consume(
        queue=settings.DEVICE_QUEUE,
        on_message_callback=callback
    )

    print("[LB] Load balancer consuming messages")
    channel.start_consuming()


@app.on_event("startup")
def startup():
    threading.Thread(target=consume, daemon=True).start()
