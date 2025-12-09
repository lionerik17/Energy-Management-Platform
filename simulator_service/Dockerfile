FROM python:3.12-slim

WORKDIR /simulator

COPY requirements.txt .
RUN pip install -r requirements.txt

COPY simulator.py .
COPY api.py .

EXPOSE 8000

CMD ["uvicorn", "api:app", "--host", "0.0.0.0", "--port", "8000"]
