# SlotlyV2

Enterprise scheduling and time-slot booking platform built with Java 21 and Spring Boot 4.0.1.

## Features

- **User Management** - Registration, login, email verification, password reset
- **Event Management** - Create events with custom availability rules and timezone support
- **Recurring Events** - Strategy-based recurrence (daily, weekly, monthly with end conditions)
- **Slot Booking** - Real-time slot booking with capacity validation and conflict prevention
- **Email Notifications** - Event-driven async emails via Resend
- **Calendar Export** - Download bookings as .ics files
- **Shareable Links** - Unique public links for event access
- **Rate Limiting** - Bucket4j-based API protection
- **Dual Authentication** - JWT (stateless) + Session-based auth

## Architecture

```
com.example.SlotlyV2/
├── feature/
│   ├── auth/           # JWT & session authentication
│   ├── availability/   # Availability rules
│   ├── calendar/       # .ics file generation
│   ├── email/          # Async email events & listeners
│   ├── event/          # Event management & recurrence strategies
│   ├── schedule/       # Schedule entities
│   ├── slot/           # Slot booking system
│   └── user/           # User management
├── config/             # Security, async, rate limiting
├── controller/         # REST endpoints
├── dto/                # Data transfer objects
├── event/              # Spring application events
├── exception/          # Custom exceptions
├── listener/           # Event listeners
├── model/              # JPA entities
├── repository/         # Data access
└── security/           # JWT filters & security
```

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 21 | Language |
| Spring Boot 4.0.1 | Framework |
| Spring Security 6.x | Authentication & authorization |
| Spring Data JPA | Database access |
| PostgreSQL | Database |
| Resend 3.0.0 | Email delivery |
| Thymeleaf | Email templates |
| JJWT 0.12.5 | JWT tokens |
| Bucket4j 8.16.0 | Rate limiting |
| Caffeine 3.2.3 | Caching |
| Lombok | Boilerplate reduction |
| JaCoCo | Test coverage |

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/users/register` | User registration |
| POST | `/api/v1/users/login` | Session login |
| POST | `/api/v1/auth/jwt/login` | JWT login |
| POST | `/api/v1/auth/jwt/refresh` | Refresh token |
| POST | `/api/v1/users/logout` | Logout |

### Events

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/events` | Create event |
| GET | `/api/v1/events` | List user events |
| GET | `/api/v1/events/{id}` | Get event details |
| DELETE | `/api/v1/events/{id}` | Delete event |
| GET | `/api/v1/events/{shareableId}` | Public event access |

### Slots & Booking

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/{shareableId}` | Get available slots |
| POST | `/api/v1/{shareableId}` | Book a slot |
| GET | `/api/v1/users/me/bookings` | User bookings |
| GET | `/api/v1/users/me/bookings/{id}/calendar` | Download .ics |

### User

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/users/password-reset/request` | Request password reset |
| POST | `/api/v1/users/password-reset/confirm` | Confirm reset |
| POST | `/api/v1/users/verify-email` | Verify email |
| GET | `/api/v1/users/me` | Get profile |

## Recurrence Strategies

Event recurrence uses the Strategy pattern:

- **Daily** - Never ends / Ends on date / Ends after N occurrences
- **Weekly** - Never ends / Ends on date / Ends after N occurrences
- **Monthly** - Never ends / Ends on date / Ends after N occurrences

## Quick Start

```bash
# Prerequisites
- Java 21
- Maven 3.9+
- PostgreSQL 15+

# Environment variables
export DATABASE_URL=jdbc:postgresql://localhost:5432/slotlyv2
export DATABASE_USERNAME=your_user
export DATABASE_PASSWORD=your_password
export JWT_SECRET_KEY=your-secret
export RESEND_API_KEY=your-resend-key
export EMAIL_FROM=noreply@example.com
export APP_BASE_URL=http://localhost:8080

# Run
./mvnw spring-boot:run

# Test
./mvnw clean test
```

## Configuration

Rate limiting settings in `application.properties`:

| Endpoint Type | Capacity | Refill Rate |
|--------------|----------|-------------|
| Global API | 100 | 1 minute |
| Login | 5 | 5 minutes |
| Registration | 3 | 1 hour |
| Booking | 10 | 1 minute |
| Password Reset | 3 | 1 hour |

## Build

```bash
# Package
./mvnw clean package -DskipTests

# Run JAR
java -jar target/SlotlyV2-0.0.1-SNAPSHOT.jar
```

## Project Stats

- 117 Java source files
- 10 test files
- Feature-based modular architecture
- Event-driven email processing
- Strategy pattern for recurrence rules

## License

MIT
