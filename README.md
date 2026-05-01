# Energy Management Platform

A modular energy management platform for clients and administrators to monitor and manage connected devices.

Clients can see their assigned devices and monitor them, while admins manage users, devices and assignments.

Built with **Spring Boot** microservices, **React (Vite)** frontend, deployed with **Docker**, and **Traefik** as the reverse proxy and API gateway.

---

## Architecture

| Component | Description |
|------------|-------------|
| **Traefik** | Reverse proxy handling routing, load balancing, and JWT-based verification |
| **RabbitMQ** | Async messaging between services |
| **Load Balancer Service** | Routes device data to monitoring replicas using hashing |
| **Auth Service** | Handles registration, login, and JWT token generation & validation |
| **User Service** | Manages users |
| **Device Service** | Manages devices and assignments to users |
| **Monitor Service (replicated 3x times)** | Stores energy consumption history |
| **Customer Support Service** | Handles chat interactions between clients and administrators, and automated logic |
| **WebSocket Service** | Listens for chat events and monitor events, and delivers real-time messages and notifications |
| **Simulator Service** | Generates live energy consumption data for devices |
| **Frontend (React)** | Dashboard for clients and admins |
| **Databases** | Each backend has its own PostgreSQL instance for isolation |

All services (except **Load Balancer Service**) run on a shared `proxy-network` behind Traefik, with messaging handled via `mq-network`.

---

## Docker Setup

Start everything with:
```bash
docker compose up --build -d
```
Stop all containers:
```bash
docker compose down
```
