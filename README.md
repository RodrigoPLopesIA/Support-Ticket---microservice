# Support Ticket Service

A **Spring Boot 3 + Java 21** microservice for managing support tickets, integrated with **PostgreSQL**, **Kafka**, **JUnit5** and **Mockito**.

---


## Clone project 
```bash 
https://github.com/RodrigoPLopesIA/Support-Ticket---microservice.git
```
## Setup

### 1. Requirements

- **Docker** and **Docker Compose**
- **Java 21** and **Maven 3.9+** (if running outside Docker)

---

### 2. Run with Docker Compose

The project includes a `docker-compose.yml` that starts:

- PostgreSQL
- Zookeeper
- Kafka
- Kafka UI
- Support Ticket Service

To build and run:

```bash
docker-compose up --build --remove-orphans
```

The application will be available at:
http://localhost:8080/

Kafka UI will be available at:
http://localhost:8082

### 3. Run Locally (without Docker)

**You need to stop the container ticket_service**

```bash
./mvnw spring-boot:run
```

### 4. Running Tests

```bash
./mvnw clean test

```
Running the tests will generate a JaCoCo coverage report.  
You can view it by opening:

**`target/site/index.html`**

### Embedded Kafka (for tests)
This avoids the need for an external Kafka during tests.
```java 
@SpringBootTest
@EmbeddedKafka(topics = {
        "ticket-events" }, partitions = 1, bootstrapServersProperty = "spring.kafka.bootstrap-servers", brokerProperties = {
                "auto.create.topics.enable=true"
        })

@Slf4j
public class ProducerServiceIntegrationTest {
    ///resto of code here
}
```
### Kafka Setup

This service depends on a Kafka topic for ticket events.
The provided docker-compose.yml already starts:

- Zookeeper (zookeeper:2181)
- Kafka (kafka:9092)
- Kafka UI (localhost:8082)

Default topic

```bash
ticket_events
```

### .env configuration:
This is the environment configuration for running the application locally.

```bash
# ============================
# Database
# ============================
POSTGRES_USER=user
POSTGRES_PASSWORD=password
POSTGRES_DB=ticket_db
POSTGRES_PORT=5432

# ============================
# Zookeeper
# ============================
ZOOKEEPER_CLIENT_PORT=2181
ZOOKEEPER_TICK_TIME=2000

# ============================
# Kafka
# ============================
KAFKA_BROKER_ID=1
KAFKA_PORT_INTERNAL=9092
KAFKA_PORT_EXTERNAL=29092
KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181
KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:${KAFKA_PORT_INTERNAL},PLAINTEXT_HOST://localhost:${KAFKA_PORT_EXTERNAL}
KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:${KAFKA_PORT_INTERNAL},PLAINTEXT_HOST://0.0.0.0:${KAFKA_PORT_EXTERNAL}
KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT
KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1

# ============================
# Kafka UI
# ============================
KAFKA_UI_PORT=8082
KAFKA_UI_INTERNAL_PORT=8080
KAFKA_UI_CLUSTER_NAME=local
KAFKA_TOPIC_TICKET_EVENTS=ticket_events
# ============================
# Spring Boot Service
# ============================
SPRING_DATASOURCE_URL=jdbc:postgresql://ticket_database:${POSTGRES_PORT}/${POSTGRES_DB}
SPRING_DATASOURCE_USERNAME=${POSTGRES_USER}
SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:${KAFKA_PORT_INTERNAL}
SPRING_APP_PORT=8080

```

### Main Endpoints

- POST /tickets → create a new ticket
- GET /tickets/{id} → fetch ticket by ID
- GET /tickets → list all tickets
- PUT /tickets/{id} → update ticket by ID
- DELETE /tickets/{id} → delete ticket by ID