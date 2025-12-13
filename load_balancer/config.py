from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    RABBITMQ_URL: str
    DEVICE_QUEUE: str
    MONITORING_REPLICAS: int

settings = Settings()
