# Awesome Pizza - Order Management System

A REST API for managing pizza orders. Customers can place orders without registration and track them via a unique tracking code. The pizzaiolo manages a FIFO queue, processing one order at a time.

## Tech Stack

- **Java 21** + **Spring Boot 3.3.3**
- **H2 Database** (in-memory, no setup required)
- **Spring Data JPA** + **Hibernate**
- **Bean Validation** for request validation
- **SpringDoc OpenAPI** for API documentation
- **JUnit 5** + **Mockito** for testing
- **Lombok** for reducing boilerplate

## Design Patterns

- **State Pattern** — Order status transitions with validation (`OrderStatus` enum)
- **Builder Pattern** — DTOs and entities via Lombok `@Builder`
- **Factory Method** — `Order.create()` generates tracking codes
- **Repository Pattern** — Spring Data JPA repositories
- **Service Layer** — Interface + Implementation separation
- **DTO Pattern** — Request/Response DTOs decoupled from domain entities
- **Strategy Pattern** — FIFO ordering via `ORDER BY createdAt ASC`

## How to Build & Run

```bash
# Build and run tests
mvn clean install

# Run the application
mvn spring-boot:run
```

The application starts on **http://localhost:8080**.

## API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **H2 Console**: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:awesomepizza`)

## API Endpoints

### Customer Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/pizzas` | Browse available pizzas |
| `GET` | `/api/pizzas/{id}` | Get pizza details |
| `POST` | `/api/orders` | Place a new order |
| `GET` | `/api/orders/{trackingCode}` | Track order by code |

### Pizzaiolo Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/pizzaiolo/orders` | View order queue (FIFO) |
| `GET` | `/api/pizzaiolo/orders/current` | Get current in-progress order |
| `PUT` | `/api/pizzaiolo/orders/{orderId}/status` | Update order status |

## Order Status Flow

```
PENDING → IN_PROGRESS → READY → PICKED_UP
```

**Business Rules:**
- Only one order can be `IN_PROGRESS` at a time
- Status transitions are strictly sequential (no skipping, no going back)
- Orders are processed in FIFO order (first come, first served)

## Example Usage

### Place an order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John",
    "items": [
      {"pizzaId": "<pizza-uuid>", "quantity": 2}
    ]
  }'
```

### Track order status
```bash
curl http://localhost:8080/api/orders/ABC12345
```

### Update order status (Pizzaiolo)
```bash
curl -X PUT http://localhost:8080/api/pizzaiolo/orders/<order-uuid>/status \
  -H "Content-Type: application/json" \
  -d '{"status": "IN_PROGRESS"}'
```

## Testing

```bash
# Run all tests
mvn test

# Run with verbose output
mvn test -Dsurefire.useFile=false
```

**Test coverage includes:**
- Unit tests for services (Mockito)
- Integration tests for controllers (MockMvc)
- Repository tests (`@DataJpaTest`)
- State machine validation tests

## Project Structure

```
src/main/java/com/awesomepizza/
├── config/          # Data initialization
├── controller/      # REST controllers
├── domain/          # Entities and enums
├── dto/             # Request/Response DTOs
├── exception/       # Custom exceptions and global handler
├── mapper/          # Entity-to-DTO mapping
├── repository/      # Spring Data JPA repositories
└── service/         # Business logic (interface + impl)
```
