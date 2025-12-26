🗓️ SlotlyV2 - Modern Scheduling Platform
A production-ready scheduling application built with Spring Boot and React, featuring event-driven architecture, JWT authentication, and real-time email notifications.
 ✨ Features
 Core Features
- ✅ **User Management** - Registration, login, logout with JWT and session-based authentication
- ✅ **Event Creation** - Create events with custom availability rules
- ✅ **Slot Generation** - Automatic time slot generation based on event duration
- ✅ **Slot Booking** - Book available slots with real-time availability
- ✅ **Email Notifications** - Async event-driven email system (host & attendee)
- ✅ **Calendar Export** - Download bookings as .ics calendar files
- ✅ **Shareable Links** - Generate unique shareable links for events
 Technical Features
- 🔄 **Event-Driven Architecture** - Decoupled email notifications via Spring events
- 🎭 **DTO Pattern** - Clean API contracts preventing lazy loading issues
- 🔐 **Dual Authentication** - JWT (stateless) + Session (traditional) support
- 🛡️ **Custom Exceptions** - Domain-specific exceptions for better error handling
- 📊 **JPA/Hibernate** - Lazy loading with `@EntityGraph` support
- ⏰ **Timezone Aware** - Proper timezone handling across all features
 🛠️ Tech Stack
 Backend
- **Java 21** - Latest LTS with modern language features
- **Spring Boot 4.0.1** - Latest framework with virtual threads
- **Spring Security 6** - OAuth2, JWT, session management
- **Spring Data JPA** - Type-safe repository abstraction
- **PostgreSQL** - Production-grade relational database
- **Resend** - Transactional email delivery service
- **Thymeleaf** - Template engine for HTML emails
- **JUnit 5** + **Mockito** - Comprehensive testing framework
- **JaCoCo** - Test coverage reporting
 Frontend (Planned)
- **React 18** - Modern UI library
- **TypeScript** - Type-safe development
- **Tailwind CSS** - Utility-first styling (planned)
 Build Tools
- **Maven** - Dependency management
- **Lombok** - Reduce boilerplate with annotations
- **Spring Boot Maven Plugin** - Application packaging
 🏗️ Architecture
 Project Structure
com.example.SlotlyV2/
├── config/              # Security, email, async configuration
├── controller/          # REST API endpoints
├── dto/                 # Data transfer objects for clean API contracts
├── exception/            # Custom exception hierarchy
├── listener/             # Event listeners for async processing
├── model/               # JPA entities with relationships
├── repository/           # Spring Data JPA repositories
├── security/             # JWT filter, token provider
└── service/             # Business logic layer
### Key Design Patterns
#### Event-Driven Architecture
SlotService.bookSlot() → publishes SlotBookedEvent
    ↓
EmailEventListener (async) → EmailService.sendBookingConfirmation()
    ↓
EmailService → Resend API
**Benefits:**
- Decouples booking logic from email logic
- Enables async processing without blocking main thread
- Easy to add more listeners (logging, analytics) without changing existing code
#### DTO Pattern for Async
SlotBookedEvent carries BookingEmailData DTO
├── Attendee details
├── Event details
├── Host details
└── Time slot information
**Benefits:**
- Extracts all data needed for emails before async execution
- Prevents lazy loading issues (no Hibernate session in async threads)
- Thread-safe immutable data transfer
### Security Architecture
#### JWT Authentication (Stateless)
- Access tokens with configurable expiration
- Refresh tokens for long-lived sessions
- `JwtAuthenticationFilter` for request interception
- Stateless REST APIs
#### Session Authentication (Traditional)
- Session-based auth for traditional UI
- `CustomUserDetailsService` loads users from database
- Compatible with existing Spring Security infrastructure
## 📚 Installation
### Prerequisites
- **Java 21** - [Download JDK](https://adoptium.net/)
- **Maven 3.6+** - Included with project
- **PostgreSQL 14+** - [Download PostgreSQL](https://www.postgresql.org/download/)
- **Node.js 18+** - For frontend (planned)
### Setup
1. **Clone Repository**
   ```bash
   git clone https://github.com/JohnRemon/SlotlyV2.git
   cd SlotlyV2
2. Configure Environment
   Create .env file in project root:
      # Database
   DATABASE_URL=jdbc:postgresql://localhost:5432/slotlyv2
   DATABASE_USERNAME=your_username
   DATABASE_PASSWORD=your_password
   # JWT
   JWT_SECRET_KEY=your-super-secret-key-at-least-32-characters-long-for-security
   JWT_ACCESS_TOKEN_EXPIRATION=900000
   JWT_REFRESH_TOKEN_EXPIRATION=604800000
   # Email
   RESEND_API_KEY=your_resend_api_key
   EMAIL_FROM_EMAIL=no-reply@yourdomain.com
   EMAIL_FROM_NAME=Slotly
   
3. Run Application
      ./mvnw spring-boot:run
   
4. Access API
   - Application: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html
   - Actuator: http://localhost:8080/actuator/health
📖 API Documentation
Endpoints
Authentication
| Method | Endpoint | Auth | Description |
|---------|-----------|------|-----------|
| Register | POST /api/users/register | None | Create new user |
| Login (Session) | POST /api/users/login | None | Session-based login |
| Login (JWT) | POST /api/auth/jwt/login | None | Get JWT tokens |
| Refresh Token | POST /api/auth/jwt/refresh | JWT | Get new access token |
| Logout | POST /api/users/logout | Session | Clear session |
Events
| Method | Endpoint | Auth | Description |
|---------|-----------|------|-----------|
| Create Event | POST /api/events | Yes | Create new event |
| Get All Events | GET /api/events | Yes | Get user's events |
| Get Event by ID | GET /api/events/{id} | Yes | Get event details |
| Delete Event | DELETE /api/events/{id} | Yes | Delete user's event |
| Get by Shareable ID | GET /api/{shareableId} | None | Public event page |
Slots
| Method | Endpoint | Auth | Description |
|---------|-----------|------|-----------|
| Get Available Slots | GET /api/slots/{shareableId} | None | Get available slots |
| Book Slot | POST /api/slots/book | Yes | Book a time slot |
| Get User Bookings | GET /api/slots/booked | Yes | Get user's bookings |
| Cancel Booking | DELETE /api/slots/{id}/cancel | Yes | Cancel booking (planned) |
| Calendar Export | GET /api/slots/{id}/calendar | None | Download .ics file |
Calendar
| Method | Endpoint | Auth | Description |
|---------|-----------|------|-----------|
| Export Calendar | GET /api/calendars/{eventId} | Yes | Get event calendar |
Users
| Method | Endpoint | Auth | Description |
|---------|-----------|------|-----------|
| Get Profile | GET /api/users/profile | Yes | Get current user info |
Interactive API Documentation
Swagger UI is available at: http://localhost:8080/swagger-ui.html
Or add this to pom.xml for auto-generated docs:
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
🧪 Testing
Test Structure
src/test/java/com/example/SlotlyV2/
├── service/
│   └── UserServiceTest.java          # 12 comprehensive tests
└── SlotlyV2ApplicationTests.java   # Spring context test
Test Coverage
Current Coverage:
- UserService: 95% instruction coverage, 83% branch coverage
- Overall Project: ~15% (expanding rapidly)
Running Tests
# Run all tests
./mvnw test
# Run specific test class
./mvnw test -Dtest=UserServiceTest
# Generate coverage report
./mvnw clean test jacoco:report
# View coverage report
open target/site/jacoco/com.example.SlotlyV2/index.html
Test Types
| Type | Tools | Purpose |
|-------|--------|---------|
| Unit Tests | JUnit 5 + Mockito | Test business logic in isolation |
| Integration Tests | Spring Boot Test + H2 | Test database operations |
| Coverage | JaCoCo 0.8.14 | Measure code coverage |
🔧 Configuration
Application Properties
Key configuration in application.properties:
# Database
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
# JWT
jwt.secret-key=${JWT_SECRET_KEY}
jwt.access-token-expiration=900000
jwt.refresh-token-expiration=604800000
# Email
resend.api-key=${RESEND_API_KEY}
email.from-email=${EMAIL_FROM_EMAIL}
email.from-name=Slotly
# Async
spring.task.execution.pool.core-pool-size=5
spring.task.execution.pool.max-pool-size=10
Security Configuration
| Feature | Implementation |
|---------|----------------|
| JWT Filter | JwtAuthenticationFilter - Intercepts requests |
| Password Encoder | BCrypt - Secure password hashing |
| Authentication Manager | Custom UserDetailsService |
| Stateless Sessions | SessionCreationPolicy.STATELESS |
| CORS | Configurable for frontend integration |
📊 Project Status
Completed Features ✅
- [x] User registration and authentication (JWT + Session)
- [x] Event creation with availability rules
- [x] Automatic slot generation
- [x] Slot booking system
- [x] Async email notifications (event-driven)
- [x] Calendar export (.ics)
- [x] Shareable links
- [x] Comprehensive test coverage (UserService: 95%)
- [x] Custom exception handling
- [x] DTO pattern for clean APIs
In Progress ⏳
- [ ] Google Calendar API integration
- [ ] Zoom meeting creation integration
- [ ] Event cancellation functionality
- [ ] maxSlotsPerUser validation
- [ ] Public/private event access control
- [ ] Email retry queue (dead letter queue)
- [ ] Test coverage for remaining services
- [ ] Swagger/OpenAPI documentation
Planned 📋
- [ ] React + TypeScript frontend
- [ ] Video conferencing integration (Google Meet)
- [ ] Advanced search and filtering
- [ ] Analytics and reporting dashboard
- [ ] Rate limiting
- [ ] Caching layer (Redis)
- [ ] CI/CD pipeline
- [ ] Docker containerization
🔄 Development Workflow
Running Locally
# Backend
./mvnw spring-boot:run
# Frontend (when implemented)
cd frontend
npm start
Testing
# Run tests
./mvnw test
# Run tests with coverage
./mvnw clean test jacoco:report
Building
# Build application
./mvnw clean package
# Skip tests during build
./mvnw clean package -DskipTests
🏆 Quality Metrics
Code Quality
| Metric | Score |
|---------|--------|
| Instruction Coverage | 95% (UserService) |
| Branch Coverage | 83% (UserService) |
| Test Pass Rate | 100% (12/12 passing) |
| Code Style | Clean, following Spring conventions |
Architecture Quality
| Aspect | Status |
|---------|--------|
| Layered Architecture | ✅ Controller → Service → Repository |
| Event-Driven Design | ✅ Decoupled email notifications |
| Dependency Injection | ✅ Constructor-based, immutable |
| Error Handling | ✅ Custom exception hierarchy |
🤝 Contributing
Getting Started
1. Fork the repository
2. Create a feature branch (git checkout -b feature/amazing-feature)
3. Make your changes
4. Run tests (./mvnw test)
5. Commit your changes (git commit -m 'Add amazing feature')
6. Push to your fork (git push origin feature/amazing-feature)
7. Open a Pull Request
Code Style
- Follow existing code conventions
- Write meaningful commit messages
- Add tests for new features
- Update documentation as needed
📄 License
This project is licensed under the MIT License - see the LICENSE (LICENSE) file for details.
👥 Credits
Built with ❤️ using:
- Spring Boot (https://spring.io/projects/spring-boot)
- Spring Security (https://spring.io/projects/spring-security)
- PostgreSQL (https://www.postgresql.org/)
- Resend (https://resend.com/)
- Maven (https://maven.apache.org/)
---
📝 Notes
Known Issues / TODOs
- Complete maxSlotsPerUser validation in booking flow
- Implement email retry queue with exponential backoff
- Add rate limiting for public endpoints
- Add comprehensive integration tests (currently ~15% coverage)
Architecture Decisions
- Event-Driven Email: Chosen for async processing and decoupling
- DTO Pattern: Solves lazy loading issues with async email threads
- Dual Authentication: Supports both modern JWT and traditional session auth
- Custom Exceptions: Provides clean API responses hiding implementation details
