#  Hotel Booking System

---

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [API Endpoints](#api-endpoints)
- [Deployment](#deployment)
- [CI/CD with GitHub Actions](#cicd-with-github-actions)

---

## Project Overview

This project is a secure and fully featured **Hotel Booking System** inspired by Booking.com. It allows users to search hotels, browse rooms, manage bookings, and perform secure authentication. Admin users can manage hotels, rooms, inventory, and generate reports.

The backend is built with **Spring Boot**, following best practices for RESTful API design, JWT-based authentication, and clean architecture.

---

## Features

- User Authentication with JWT (access & refresh tokens)
- Search hotels by city, dates, and room count
- Paginated search results with price and availability
- Admin CRUD operations for hotels and rooms
- Inventory management for room availability
- Booking management and reporting features
- Secure authentication with refresh tokens stored as HTTP-only cookies
- Swagger/OpenAPI integration for API documentation

---

## Tech Stack

| Layer            | Technology                        |
|------------------|-----------------------------------|
| Language         | Java 17                           |
| Framework        | Spring Boot 3                     |
| Security         | Spring Security, JWT              |
| Data Access      | Spring Data JPA, Hibernate        |
| Database         | PostgreSQL                        |
| API Docs         | OpenAPI (Swagger v3)              |
| Build Tool       | Maven                             |
| Containerization | Docker (optional)                 |
| Cloud            | Azure                             |

---

## Architecture

- Controller Layer: REST endpoints grouped by functionality
- Service Layer: Business logic and transaction management
- Repository Layer: Data access using Spring Data JPA
- Security: JWT authentication with refresh token management
- DTOs: Request and response objects for clean API contracts

---

## API Endpoints (Highlights)

| Method | Endpoint                         | Description                                | Security         |
|--------|----------------------------------|--------------------------------------------|------------------|
| POST   | `/auth/signup`                   | Register new user                          | No               |
| POST   | `/auth/login`                    | Login user, return JWT access token        | No               |
| POST   | `/auth/refresh`                  | Refresh access token using refresh token   | No (uses cookie) |
| GET    | `/hotels/search`                 | Search hotels by city and date             | No               |
| GET    | `/hotels/{id}/info`              | Get detailed info of a hotel               | No               |
| POST   | `/admin/hotels`                  | Create new hotel (Admin)                   | Yes              |
| GET    | `/admin/hotels/{id}`             | Retrieve hotel by ID (Admin)               | Yes              |
| PUT    | `/admin/hotels/{id}`             | Update hotel details (Admin)               | Yes              |
| DELETE | `/admin/hotels/{id}`             | Delete hotel by ID (Admin)                 | Yes              |
| PATCH  | `/admin/hotels/{id}/activate`    | Activate/deactivate hotel (Admin)          | Yes              |
| GET    | `/admin/hotels/{id}/bookings`    | Get all bookings for a hotel (Admin)       | Yes              |
| GET    | `/admin/hotels/{id}/reports`     | Get booking reports between dates (Admin)  | Yes              |
| POST   | `/admin/hotels/{hotelId}/rooms`  | Create a room under a hotel (Admin)        | Yes              |
| GET    | `/admin/hotels/{hotelId}/rooms`  | List rooms in a hotel (Admin)              | Yes              |
| PATCH  | `/admin/inventory/rooms/{roomId}`| Update inventory for a room (Admin)        | Yes              |

## Deployment

This Hotel Booking System is deployed on **Microsoft Azure App Service**, a reliable cloud hosting environment.

### Deployment Details

- **Cloud Provider:** Microsoft Azure
- **Hosting Service:** Azure App Service
- **Database:** Azure Database for PostgreSQL
- **Deployment Artifact:** Packaged as a runnable JAR and deployed via Azure App Service

## CI/CD with GitHub Actions

This project uses **GitHub Actions** for Continuous Integration and Continuous Deployment (CI/CD) to automate builds and deployments to Azure App Service.


Swagger UI available at: `https://bookingclone.azurewebsites.net/api/v1/swagger-ui/index.html`

---
