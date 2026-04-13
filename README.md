# AI Briefing — Spring Boot Application

A Spring Boot 3.2 application for managing AI-generated briefings and news digests.

# 1. Start the app (no keys needed — uses mock data)
mvn spring-boot:run

# 2. Subscribe yourself
curl -X POST http://localhost:8080/api/v1/users/subscribe \
-H "Content-Type: application/json" \
-d '{"name":"You","email":"you@test.com","topics":["AI tools","Stock market news"],"deliveryTimes":["09:00","13:00"],"persona":"FOUNDER"}'

# 3. Get your briefing NOW (don't wait for the scheduler)
curl -X POST http://localhost:8080/api/v1/brief/generate/you@test.com

# 4. Check delivery history
curl http://localhost:8080/api/v1/brief/history/you@test.com

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
