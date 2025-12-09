# DS Assignment 2

A modular energy management platform for clients and administrators to monitor and manage connected devices.

Clients can see their assigned devices and monitor them, while admins manage users, devices and assignments.

Built with **Spring Boot** microservices, **React (Vite)** frontend, deployed with **Docker**, and **Traefik** as the reverse proxy and API gateway.

---

## Architecture

| Component | Description |
|------------|-------------|
| **Traefik** | Reverse proxy handling routing, load balancing, and JWT-based verification |
| **RabbitMQ** | Async messaging between services |
| **Auth Service** | Handles registration, login, and JWT token generation & validation |
| **User Service** | Manages users |
| **Device Service** | Manages devices and assignments to users |
| **Monitor Service** | Stores energy consumption history |
| **Simulator Service** | Generates live energy consumption data for devices |
| **Frontend (React)** | Dashboard for clients and admins |
| **Databases** | Each backend has its own PostgreSQL instance for isolation |

All services run on a shared `proxy-network` behind Traefik, with messaging handled via `mq-network`.

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