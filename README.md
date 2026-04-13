# AI Briefing — Spring Boot Application

A production-ready Spring Boot 3.2 application for managing AI-generated briefings and news digests.

---

## Project Structure

```
com.ai.briefing
├── controller       BriefingController           REST API endpoints
├── service          BriefingService (interface)
│                    BriefingServiceImpl           Business logic
├── scheduler        BriefingScheduler             Auto-publish cron jobs
├── repository       BriefingRepository            JPA data access
│                    BriefingItemRepository
├── model            Briefing, BriefingItem        JPA entities
│                    BriefingStatus, BriefingCategory  Enums
├── dto              BriefingRequestDTO            Input validation
│                    BriefingResponseDTO           API responses
│                    BriefingItemRequestDTO
│                    BriefingItemResponseDTO
│                    ApiResponse<T>                Generic response wrapper
└── config           AppConfig                     Beans (ObjectMapper, RestTemplate)
                     OpenApiConfig                 Swagger / OpenAPI setup
                     GlobalExceptionHandler        Centralised error handling
                     DataSeeder                    Sample data on startup
```

---

## Tech Stack

| Layer       | Technology                              |
|-------------|-----------------------------------------|
| Framework   | Spring Boot 3.2, Java 17                |
| Persistence | Spring Data JPA, Hibernate              |
| Database    | H2 (dev) — swap for PostgreSQL in prod  |
| Validation  | Jakarta Bean Validation                 |
| Docs        | SpringDoc OpenAPI 2 (Swagger UI)        |
| Scheduling  | Spring `@Scheduled`                     |
| Utilities   | Lombok                                  |
| Testing     | JUnit 5, Spring Boot Test               |

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+

### Run
```bash
mvn spring-boot:run
```

### Test
```bash
mvn test
```

### Build JAR
```bash
mvn clean package
java -jar target/briefing-1.0.0.jar
```

---

## API Endpoints

| Method | Endpoint                                | Description                  |
|--------|-----------------------------------------|------------------------------|
| POST   | `/api/v1/briefings`                     | Create a briefing            |
| GET    | `/api/v1/briefings`                     | Get all briefings            |
| GET    | `/api/v1/briefings/{id}`               | Get briefing by ID           |
| GET    | `/api/v1/briefings/status/{status}`    | Filter by status             |
| GET    | `/api/v1/briefings/category/{cat}`     | Filter by category           |
| GET    | `/api/v1/briefings/search?keyword=`    | Full-text search             |
| PUT    | `/api/v1/briefings/{id}`               | Update a briefing            |
| PATCH  | `/api/v1/briefings/{id}/publish`       | Publish a briefing           |
| PATCH  | `/api/v1/briefings/{id}/archive`       | Archive a briefing           |
| DELETE | `/api/v1/briefings/{id}`               | Delete a briefing            |

---

## Useful URLs (local)

| URL                                    | Description          |
|----------------------------------------|----------------------|
| http://localhost:8080/swagger-ui.html  | Swagger UI           |
| http://localhost:8080/api-docs         | Raw OpenAPI JSON     |
| http://localhost:8080/h2-console       | H2 database console  |

**H2 console settings:** JDBC URL `jdbc:h2:mem:briefingdb` · User `sa` · Password *(blank)*

---

## Sample Request

```json
POST /api/v1/briefings
{
  "title": "Morning AI Digest",
  "summary": "Today's top stories in artificial intelligence.",
  "category": "TECHNOLOGY",
  "createdBy": "editorial-bot",
  "items": [
    {
      "headline": "New LLM Achieves Human-Level Reasoning",
      "content": "Researchers published results showing...",
      "source": "Nature",
      "sourceUrl": "https://nature.com/articles/...",
      "priority": 1
    }
  ]
}
```

---

## Switching to PostgreSQL

Update `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/briefingdb
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

Add the driver dependency to `pom.xml`:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```
