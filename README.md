# Awesome Pizza - Order Management System

A full-stack application for managing pizza orders. Customers can browse the menu, place orders without registration, and track them via a unique tracking code. The pizzaiolo manages a FIFO queue, processing one order at a time.

## Tech Stack

### Backend
- **Java 21** + **Spring Boot 3.3.3**
- **H2 Database** (in-memory, no setup required)
- **Spring Data JPA** + **Hibernate**
- **Bean Validation** for request validation
- **SpringDoc OpenAPI** for API documentation
- **JUnit 5** + **Mockito** for testing
- **Lombok** for reducing boilerplate

### Frontend
- **React 18** + **TypeScript** (via **Vite**)
- **React Router v6** for client-side routing
- **Axios** for HTTP requests
- **Framer Motion** for animations and transitions
- **CSS Modules** + CSS custom properties for scoped theming
- **react-hot-toast** for notifications

## Design Patterns

- **State Pattern** — Order status transitions with validation (`OrderStatus` enum)
- **Builder Pattern** — DTOs and entities via Lombok `@Builder`
- **Factory Method** — `Order.create()` generates tracking codes
- **Repository Pattern** — Spring Data JPA repositories
- **Service Layer** — Interface + Implementation separation
- **DTO Pattern** — Request/Response DTOs decoupled from domain entities
- **Strategy Pattern** — FIFO ordering via `ORDER BY createdAt ASC`
- **Context Pattern** — React Context + useReducer for cart state management
- **Polling Pattern** — Custom `usePolling` hook for real-time order tracking

## Prerequisites

- **Java 17+** (tested with Java 18)
- **Node.js 20+** and **npm**
- **Maven** (included via Maven Wrapper)

## How to Build & Run

### Backend

```bash
# Build and run tests
mvn clean install

# Run the application
mvn spring-boot:run
```

Or use the provided startup scripts:

```bash
# Linux/macOS/Git Bash
bash start.sh

# Windows
start.bat
```

The backend starts on **http://localhost:8080**.

### Frontend

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend starts on **http://localhost:5173**.

> **Note:** Start the backend first. The Vite dev server proxies all `/api` requests to `http://localhost:8080` automatically.

### Production Build

```bash
cd frontend
npm run build
```

The built files are output to `frontend/dist/`.

## Application Features

### Customer View (`/`)
- **Pizza Carousel** — Horizontally scrollable menu with animated cards, scroll-snap, and hover effects
- **Shopping Cart** — Floating cart button with slide-out drawer, quantity controls, and subtotal
- **Order Placement** — Optional customer name, instant order submission with tracking code confirmation
- **Order Tracking** — Enter a tracking code to see a real-time 4-step status timeline (auto-refreshes every 5 seconds)

### Pizzaiolo Dashboard (`/pizzaiolo`)
- **Current Order** — Highlighted card showing the order currently being prepared
- **Order Queue** — FIFO list of pending and ready orders with context-aware action buttons
- **Status Management** — One-click status transitions: Start Preparing → Mark Ready → Mark Picked Up
- **Auto-Refresh** — Queue updates automatically every 3 seconds

### Dedicated Tracking Page (`/track`)
- Standalone page for order tracking with URL parameter support (`/track?code=ABC12345`)

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
awesome-pizza/
├── src/main/java/com/awesomepizza/
│   ├── config/          # CORS config, data initialization
│   ├── controller/      # REST controllers (Order, Pizza, Pizzaiolo, Home)
│   ├── domain/          # Entities (Order, OrderItem, Pizza) and enums (OrderStatus)
│   ├── dto/             # Request/Response DTOs
│   ├── exception/       # Custom exceptions and global handler
│   ├── mapper/          # Entity-to-DTO mapping
│   ├── repository/      # Spring Data JPA repositories
│   └── service/         # Business logic (interface + impl)
├── src/test/java/       # Unit and integration tests
├── frontend/
│   ├── src/
│   │   ├── api/         # Axios HTTP client and API functions
│   │   ├── types/       # TypeScript interfaces matching backend DTOs
│   │   ├── context/     # Cart state management (Context + useReducer)
│   │   ├── hooks/       # Custom hooks (usePolling for auto-refresh)
│   │   ├── components/
│   │   │   ├── layout/      # Navbar
│   │   │   ├── customer/    # PizzaCarousel, PizzaCard, Cart, OrderTracker, OrderConfirmation
│   │   │   └── pizzaiolo/   # OrderQueue, OrderCard, CurrentOrder, StatusBadge
│   │   ├── pages/       # CustomerPage, TrackPage, PizzaioloPage
│   │   ├── utils/       # Formatting helpers (currency, date)
│   │   └── styles/      # Global CSS with theme variables
│   └── package.json
├── start.sh             # Startup script (Linux/macOS/Git Bash)
├── start.bat            # Startup script (Windows)
└── pom.xml
```
