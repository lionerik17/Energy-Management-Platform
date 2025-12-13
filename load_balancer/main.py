import json
import threading
import pika
from fastapi import FastAPI

from config import settings
from router import select_replica

app = FastAPI(title="Load Balancer Service")


def consume():
    connection = pika.BlockingConnection(
        pika.URLParameters(settings.RABBITMQ_URL)
    )
    channel = connection.channel()
    channel.queue_declare(queue=settings.DEVICE_QUEUE, durable=True)

    def callback(ch, method, properties, body):
        msg = json.loads(body)
        device_id = msg.get("deviceId")

        if not device_id:
            ch.basic_ack(method.delivery_tag)
            return

        replica = select_replica(device_id)
        ingest_queue = f"ingest.queue.{replica}"

        channel.basic_publish(
            exchange="",
            routing_key=ingest_queue,
            body=json.dumps(msg),
            properties=pika.BasicProperties(delivery_mode=2)
        )

        print(f"[LB] {device_id} replica {replica}")
        ch.basic_ack(method.delivery_tag)

    channel.basic_consume(
        queue=settings.DEVICE_QUEUE,
        on_message_callback=callback
    )

    channel.start_consuming()


@app.on_event("startup")
def startup():
    threading.Thread(target=consume, daemon=True).start()
