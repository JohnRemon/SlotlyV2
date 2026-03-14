# SlotlyV2

SlotlyV2 is a full-stack scheduling and booking application with a Spring Boot backend and a React + Vite frontend. It supports authenticated scheduling workflows, public booking links, Google sign-in, Google Calendar connection, and email-based account flows.

## What It Does

- Create and manage booking events
- Configure schedules and blocked periods
- Share public booking links and let guests reserve slots
- Manage bookings with timezone-aware availability
- Authenticate with email/password or Google OAuth
- Verify email addresses and reset passwords by email
- Connect a Google Calendar account from the app

## Stack

### Backend

- Java 21
- Spring Boot 4
- Spring Security
- Spring Data JPA
- PostgreSQL
- Maven
- Resend for transactional email
- Google OAuth / Google Calendar APIs
- Bucket4j + Caffeine for rate limiting

### Frontend

- React 19
- TypeScript
- Vite
- React Router
- TanStack Query
- Tailwind CSS 4
- React Hook Form + Zod
- Google OAuth client

## Repository Layout

```text
.
├── backend/   # Spring Boot API, auth, booking logic, integrations
└── frontend/  # React app, routed product UI, API client
```

## Product Areas

The current app surface is split across these main areas:

- `Events` - create and edit booking event definitions
- `Bookings` - review booking records
- `Schedules` - manage working hours and schedule details
- `Apps` - connect external services such as Google Calendar
- `Settings` - manage account/profile data
- Public booking flow - guest-facing booking page via shareable links

## Architecture Notes

- The frontend uses `axios` with `withCredentials: true` and relies on backend session auth for the main web app.
- The backend stores authenticated sessions with Spring Security's `HttpSessionSecurityContextRepository`.
- The backend also includes JWT auth endpoints, but the current frontend app flow is session-oriented.
- Backend configuration loads local environment variables from `backend/.env` via Spring config import.

## Prerequisites

Before running locally, install:

- Java 21
- Maven 3.9+
- Node.js and npm
- PostgreSQL

## Environment Variables

Do not commit real credentials. Use local `.env` files only.

### Backend

The backend reads configuration from `backend/.env` and `backend/src/main/resources/application.properties`.

Required values used by the app:

```env
DATABASE_URL=jdbc:postgresql://localhost:5432/slotlyv2
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your-password
JWT_SECRET_KEY=replace-with-a-long-random-secret
RESEND_API_KEY=your-resend-api-key
EMAIL_FROM=noreply@example.com
APP_BASE_URL=http://localhost:8080
APP_FRONTEND_URL=http://localhost:5173
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
GOOGLE_REDIRECT_URI=http://localhost:5173/apps
```

### Frontend

The frontend reads configuration from `frontend/.env`.

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_GOOGLE_CLIENT_ID=your-google-client-id
```

## Local Development

Run the backend and frontend in separate terminals.

### 1. Start the backend

From `backend/`:

```bash
mvn spring-boot:run
```

The API runs on `http://localhost:8080` by default.

### 2. Start the frontend

From `frontend/`:

```bash
npm install
npm run dev
```

The Vite dev server runs on `http://localhost:5173` by default.

## Common Commands

### Backend

From `backend/`:

```bash
mvn test
mvn clean package
```

### Frontend

From `frontend/`:

```bash
npm run dev
npm run build
npm run lint
npm run preview
```

## API Surface Overview

The backend currently exposes endpoints for:

- user registration and profile retrieval
- session login/logout and JWT auth endpoints
- email verification and password reset
- event CRUD and event scheduling updates
- schedule CRUD and blocked periods
- slot lookup and public slot availability
- booking creation and management
- Google Calendar connection status and OAuth exchange

See controllers under `backend/src/main/java/com/example/SlotlyV2/feature/` for the current endpoint layout.

## Frontend Routing Overview

The main frontend routes are defined in `frontend/src/App.tsx` and include:

- `/login`
- `/register`
- `/forgot-password`
- `/verify-email`
- `/book/:shareableId`
- `/events`
- `/bookings`
- `/schedules`
- `/apps`
- `/settings`

## Notes for Contributors

- Backend code is organized by feature under `backend/src/main/java/com/example/SlotlyV2/feature`.
- Frontend code is organized by feature under `frontend/src/features`.
- There is no root-level dev orchestrator script today; run frontend and backend separately.
- The checked-in `frontend/README.md` is still the default Vite template; this root README is the project-level source of truth.

## Status

This repository is actively evolving. If you are onboarding to the project, treat the codebase as the authoritative source for current behavior and endpoint details.
