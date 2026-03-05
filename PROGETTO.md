# Awesome Pizza - Documentazione Completa del Progetto

## Indice
1. [Panoramica](#panoramica)
2. [Requisiti e Avvio](#requisiti-e-avvio)
3. [Stack Tecnologico](#stack-tecnologico)
4. [Struttura del Progetto](#struttura-del-progetto)
5. [Configurazione](#configurazione)
6. [Architettura](#architettura)
7. [Domain Layer (Entità ed Enum)](#domain-layer)
8. [Repository Layer](#repository-layer)
9. [Service Layer](#service-layer)
10. [Controller Layer (API REST)](#controller-layer)
11. [DTO (Request e Response)](#dto)
12. [Mapper](#mapper)
13. [Gestione Eccezioni](#gestione-eccezioni)
14. [Dati Iniziali](#dati-iniziali)
15. [Test](#test)
16. [Comandi Utili](#comandi-utili)
17. [Endpoint API Completi](#endpoint-api-completi)
18. [Flusso Completo di un Ordine](#flusso-completo-di-un-ordine)

---

## Panoramica

Sistema REST API per la gestione degli ordini di una pizzeria.
- I **clienti** piazzano ordini senza registrazione e li tracciano tramite un codice di 8 caratteri.
- Il **pizzaiolo** gestisce una coda FIFO e lavora un ordine alla volta.

---

## Requisiti e Avvio

### Requisiti
- **Java 17+** (sul sistema è presente Java 18 in `C:\Program Files\Java\jdk-18`)
- Maven (incluso tramite Maven Wrapper `mvnw`)

### Comandi per avviare

```bash
# Da Git Bash / terminale
export JAVA_HOME="/c/Program Files/Java/jdk-18"
./mvnw spring-boot:run
```

L'app si avvia su **http://localhost:8080** e reindirizza a Swagger UI.

### Comandi utili

```bash
# Compilare senza avviare
./mvnw compile

# Eseguire i test
./mvnw test

# Creare il JAR
./mvnw package

# Avviare dal JAR
java -jar target/awesome-pizza-0.0.1-SNAPSHOT.jar

# Pulire la build
./mvnw clean
```

---

## Stack Tecnologico

| Tecnologia | Versione | Scopo |
|---|---|---|
| Spring Boot | 3.3.3 | Framework principale |
| Spring Web | - | REST API |
| Spring Data JPA | - | Accesso dati / ORM |
| Spring Validation | - | Validazione input (`@Valid`, `@NotNull`, ecc.) |
| Hibernate | 6.5.2 | ORM (implementazione JPA) |
| H2 Database | - | Database in-memory (sviluppo) |
| Lombok | - | Riduzione boilerplate (getter, setter, builder, ecc.) |
| Springdoc OpenAPI | 2.3.0 | Documentazione Swagger UI |
| JUnit 5 + Mockito | - | Testing |

---

## Struttura del Progetto

```
awesome-pizza/
├── pom.xml                          # Configurazione Maven e dipendenze
├── mvnw / .mvn/                     # Maven Wrapper
├── src/
│   ├── main/
│   │   ├── java/com/awesomepizza/
│   │   │   ├── AwesomePizzaApplication.java        # Entry point
│   │   │   ├── config/
│   │   │   │   └── DataInitializer.java            # Seed dati iniziali (5 pizze)
│   │   │   ├── controller/
│   │   │   │   ├── HomeController.java             # Redirect / → Swagger
│   │   │   │   ├── OrderController.java            # API cliente (ordini)
│   │   │   │   ├── PizzaController.java            # API menu pizze
│   │   │   │   └── PizzaioloController.java        # API pizzaiolo (gestione coda)
│   │   │   ├── domain/
│   │   │   │   ├── entity/
│   │   │   │   │   ├── BaseEntity.java             # Superclasse con UUID
│   │   │   │   │   ├── Order.java                  # Ordine
│   │   │   │   │   ├── OrderItem.java              # Riga dell'ordine
│   │   │   │   │   └── Pizza.java                  # Pizza nel menu
│   │   │   │   └── enums/
│   │   │   │       └── OrderStatus.java            # Macchina a stati
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── CreateOrderRequest.java     # Body per creare ordine
│   │   │   │   │   ├── OrderItemRequest.java       # Singola pizza nell'ordine
│   │   │   │   │   └── UpdateOrderStatusRequest.java # Cambio stato
│   │   │   │   └── response/
│   │   │   │       ├── OrderItemResponse.java      # Dettaglio riga ordine
│   │   │   │       ├── OrderResponse.java          # Ordine completo (per pizzaiolo)
│   │   │   │       ├── OrderTrackingResponse.java  # Tracking ordine (per cliente)
│   │   │   │       └── PizzaResponse.java          # Pizza nel menu
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java     # Gestione centralizzata errori
│   │   │   │   ├── InvalidStatusTransitionException.java
│   │   │   │   ├── OrderAlreadyInProgressException.java
│   │   │   │   ├── OrderNotFoundException.java
│   │   │   │   └── PizzaNotFoundException.java
│   │   │   ├── mapper/
│   │   │   │   └── OrderMapper.java                # Entity → DTO conversioni
│   │   │   ├── repository/
│   │   │   │   ├── OrderItemRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   └── PizzaRepository.java
│   │   │   └── service/
│   │   │       ├── OrderService.java               # Interfaccia
│   │   │       ├── PizzaService.java               # Interfaccia
│   │   │       └── impl/
│   │   │           ├── OrderServiceImpl.java        # Logica ordini
│   │   │           └── PizzaServiceImpl.java        # Logica pizze
│   │   └── resources/
│   │       └── application.yml                      # Configurazione app
│   └── test/java/com/awesomepizza/
│       ├── controller/
│       │   ├── OrderControllerTest.java
│       │   ├── PizzaControllerTest.java
│       │   └── PizzaioloControllerTest.java
│       ├── domain/enums/
│       │   └── OrderStatusTest.java
│       ├── repository/
│       │   └── OrderRepositoryTest.java
│       └── service/
│           ├── OrderServiceTest.java
│           └── PizzaServiceTest.java
```

---

## Configurazione

File: `src/main/resources/application.yml`

```yaml
spring:
  application:
    name: awesome-pizza
  datasource:
    url: jdbc:h2:mem:awesomepizza    # Database in-memory (dati persi al riavvio)
    driver-class-name: org.h2.Driver
    username: sa
    password:                         # Nessuna password
  jpa:
    hibernate:
      ddl-auto: create-drop          # Schema ricreato ad ogni avvio
    show-sql: true                    # Log delle query SQL
  h2:
    console:
      enabled: true
      path: /h2-console              # Console DB accessibile da browser
server:
  port: 8080
springdoc:
  swagger-ui:
    path: /swagger-ui.html
  api-docs:
    path: /api-docs
```

**Note:**
- `create-drop`: Hibernate crea le tabelle all'avvio e le elimina allo stop. Ogni riavvio riparte da zero.
- La console H2 è raggiungibile su `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:awesomepizza`)

---

## Architettura

L'architettura segue un pattern **a strati** (layered architecture):

```
╔══════════════════════════════════════════════════════╗
║  CLIENT (Browser / Postman / curl)                   ║
╚══════════════════╤═══════════════════════════════════╝
                   │ HTTP Request
╔══════════════════▼═══════════════════════════════════╗
║  CONTROLLER LAYER (@RestController)                   ║
║  - Riceve le richieste HTTP                           ║
║  - Valida l'input (@Valid)                            ║
║  - Delega al Service                                  ║
║  - Restituisce ResponseEntity con status HTTP         ║
╠══════════════════════════════════════════════════════╣
║  SERVICE LAYER (@Service, @Transactional)             ║
║  - Contiene la logica di business                     ║
║  - Gestisce le transazioni                            ║
║  - Coordina repository e mapper                       ║
╠══════════════════════════════════════════════════════╣
║  REPOSITORY LAYER (JpaRepository)                     ║
║  - Accesso ai dati (CRUD + query custom)              ║
║  - Spring Data genera le implementazioni              ║
╠══════════════════════════════════════════════════════╣
║  DOMAIN LAYER (Entity + Enum)                         ║
║  - Entità JPA mappate sulle tabelle                   ║
║  - Logica di dominio (es. macchina a stati)           ║
╠══════════════════════════════════════════════════════╣
║  DATABASE (H2 in-memory)                              ║
║  Tabelle: pizza, pizza_order, order_item              ║
╚══════════════════════════════════════════════════════╝
```

**Flusso di una richiesta:**
1. Il client invia una richiesta HTTP (es. `POST /api/orders`)
2. Il **Controller** riceve, valida l'input, e chiama il Service
3. Il **Service** esegue la logica di business, usa i Repository per leggere/scrivere dati
4. Il **Repository** interroga il database tramite JPA/Hibernate
5. Il **Mapper** converte le Entity in DTO di risposta
6. Il Controller restituisce la risposta HTTP al client

---

## Domain Layer

### BaseEntity
Superclasse astratta. Tutte le entità ereditano da qui.

```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;   // Generato automaticamente
}
```

### Pizza
Rappresenta una pizza nel menu.

| Campo | Tipo | Vincoli |
|---|---|---|
| id | UUID | PK, auto-generato |
| name | String | NOT NULL |
| description | String | opzionale |
| price | BigDecimal | NOT NULL |

### Order
Rappresenta un ordine. Tabella: `pizza_order` (evita la parola riservata SQL "order").

| Campo | Tipo | Vincoli |
|---|---|---|
| id | UUID | PK, auto-generato |
| trackingCode | String | UNIQUE, NOT NULL (8 caratteri) |
| customerName | String | opzionale |
| status | OrderStatus | NOT NULL (enum come STRING) |
| createdAt | LocalDateTime | NOT NULL, non modificabile |
| updatedAt | LocalDateTime | NOT NULL |
| items | List<OrderItem> | OneToMany, cascade ALL |

**Factory method:** `Order.create(customerName)` crea un ordine con stato `PENDING`, genera il tracking code (primi 8 caratteri di un UUID random in maiuscolo), e setta i timestamp.

### OrderItem
Riga di un ordine (collegamento ordine-pizza con quantità).

| Campo | Tipo | Vincoli |
|---|---|---|
| id | UUID | PK, auto-generato |
| order | Order | FK, ManyToOne LAZY |
| pizza | Pizza | FK, ManyToOne LAZY |
| quantity | Integer | NOT NULL |

### OrderStatus (Macchina a Stati)

```
PENDING ──→ IN_PROGRESS ──→ READY ──→ PICKED_UP
```

Ogni stato definisce le transizioni valide:

| Stato | Può andare a | Significato |
|---|---|---|
| `PENDING` | `IN_PROGRESS` | Ordine in attesa nella coda |
| `IN_PROGRESS` | `READY` | Il pizzaiolo sta lavorando l'ordine |
| `READY` | `PICKED_UP` | Pizza pronta per il ritiro |
| `PICKED_UP` | (nessuno) | Ordine completato (stato terminale) |

**Regole:**
- Non si possono saltare stati (es. PENDING → READY è vietato)
- Non si può tornare indietro (es. READY → IN_PROGRESS è vietato)
- Un solo ordine alla volta può essere IN_PROGRESS

---

## Repository Layer

### PizzaRepository
```java
public interface PizzaRepository extends JpaRepository<Pizza, UUID> { }
```
Solo operazioni CRUD standard.

### OrderRepository
```java
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByTrackingCode(String trackingCode);
    List<Order> findByStatusOrderByCreatedAtAsc(OrderStatus status);
    List<Order> findByStatusInOrderByCreatedAtAsc(List<OrderStatus> statuses);
    Optional<Order> findFirstByStatus(OrderStatus status);
    boolean existsByStatus(OrderStatus status);
}
```
- **FIFO**: `OrderByCreatedAtAsc` ordina per data di creazione (il più vecchio prima)
- `existsByStatus(IN_PROGRESS)` verifica se c'è già un ordine in lavorazione

### OrderItemRepository
```java
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> { }
```
Non usato direttamente — gli item vengono gestiti tramite la cascade di Order.

---

## Service Layer

### PizzaService / PizzaServiceImpl
- `getAllPizzas()` → Restituisce tutte le pizze come `List<PizzaResponse>`
- `getPizzaById(UUID id)` → Restituisce una pizza o lancia `PizzaNotFoundException`

### OrderService / OrderServiceImpl

#### `createOrder(CreateOrderRequest request)`
1. Crea l'ordine con `Order.create(customerName)` (stato PENDING, tracking code generato)
2. Per ogni item nella richiesta, cerca la Pizza per ID (errore se non trovata)
3. Crea `OrderItem` e lo aggiunge all'ordine
4. Salva l'ordine (cascade salva anche gli item)
5. Restituisce `OrderTrackingResponse` con tracking code

#### `getOrderByTrackingCode(String trackingCode)`
- Cerca per tracking code, lancia `OrderNotFoundException` se non trovato
- Restituisce `OrderTrackingResponse` (solo info di tracking, non i dettagli)

#### `getOrderQueue()`
- Restituisce tutti gli ordini con stato PENDING, IN_PROGRESS o READY
- Ordinati per `createdAt` crescente (FIFO)
- Restituisce `List<OrderResponse>` con tutti i dettagli

#### `getCurrentOrder()`
- Restituisce l'ordine attualmente IN_PROGRESS (o null se nessuno)

#### `updateOrderStatus(UUID orderId, UpdateOrderStatusRequest request)`
1. Trova l'ordine per ID
2. Verifica che la transizione sia valida (tramite `OrderStatus.canTransitionTo()`)
3. Se si sta passando a IN_PROGRESS, verifica che non ci sia già un altro ordine in lavorazione
4. Aggiorna stato e timestamp
5. Restituisce `OrderResponse` aggiornato

---

## Controller Layer

### HomeController — `GET /`
Redirect a Swagger UI (`/swagger-ui/index.html`).

### OrderController — `/api/orders` (per il cliente)

| Metodo | Endpoint | Descrizione | Risposta |
|---|---|---|---|
| POST | `/api/orders` | Crea un nuovo ordine | 201 + `OrderTrackingResponse` |
| GET | `/api/orders/{trackingCode}` | Traccia un ordine | 200 + `OrderTrackingResponse` |

### PizzaController — `/api/pizzas` (per il cliente)

| Metodo | Endpoint | Descrizione | Risposta |
|---|---|---|---|
| GET | `/api/pizzas` | Lista tutte le pizze | 200 + `List<PizzaResponse>` |
| GET | `/api/pizzas/{id}` | Dettaglio una pizza | 200 + `PizzaResponse` |

### PizzaioloController — `/api/pizzaiolo/orders` (per il pizzaiolo)

| Metodo | Endpoint | Descrizione | Risposta |
|---|---|---|---|
| GET | `/api/pizzaiolo/orders` | Coda ordini (FIFO) | 200 + `List<OrderResponse>` |
| GET | `/api/pizzaiolo/orders/current` | Ordine in lavorazione | 200 + `OrderResponse` oppure 204 |
| PUT | `/api/pizzaiolo/orders/{orderId}/status` | Aggiorna stato ordine | 200 + `OrderResponse` |

---

## DTO

### Request

**CreateOrderRequest** (body del POST /api/orders)
```json
{
  "customerName": "Mario Rossi",        // opzionale
  "items": [                             // obbligatorio, non vuoto
    { "pizzaId": "uuid-...", "quantity": 2 },
    { "pizzaId": "uuid-...", "quantity": 1 }
  ]
}
```

**UpdateOrderStatusRequest** (body del PUT .../status)
```json
{
  "status": "IN_PROGRESS"    // obbligatorio: IN_PROGRESS, READY, o PICKED_UP
}
```

### Response

**PizzaResponse**
```json
{ "id": "uuid", "name": "Margherita", "description": "...", "price": 8.50 }
```

**OrderTrackingResponse** (per il cliente — info minime)
```json
{
  "trackingCode": "A1B2C3D4",
  "status": "PENDING",
  "createdAt": "2026-03-04T...",
  "updatedAt": "2026-03-04T..."
}
```

**OrderResponse** (per il pizzaiolo — dettaglio completo)
```json
{
  "id": "uuid",
  "trackingCode": "A1B2C3D4",
  "customerName": "Mario Rossi",
  "status": "PENDING",
  "createdAt": "2026-03-04T...",
  "updatedAt": "2026-03-04T...",
  "items": [
    { "pizzaId": "uuid", "pizzaName": "Margherita", "quantity": 2, "price": 8.50 }
  ]
}
```

**Scelta di design:** Il cliente vede solo `OrderTrackingResponse` (senza ID interno, senza dettagli items). Il pizzaiolo vede `OrderResponse` completo. Questo è un pattern di **information hiding**.

---

## Mapper

`OrderMapper` è un `@Component` con metodi manuali (niente MapStruct):

- `toOrderResponse(Order)` → `OrderResponse` (con items)
- `toTrackingResponse(Order)` → `OrderTrackingResponse`
- `toPizzaResponse(Pizza)` → `PizzaResponse`
- `toOrderResponseList(List<Order>)` → `List<OrderResponse>`

---

## Gestione Eccezioni

`GlobalExceptionHandler` (`@RestControllerAdvice`) cattura le eccezioni e restituisce risposte JSON uniformi:

```json
{
  "timestamp": "2026-03-04T19:40:00",
  "status": 404,
  "error": "Not Found",
  "message": "Order not found with tracking code: A1B2C3D4"
}
```

| Eccezione | HTTP Status | Quando |
|---|---|---|
| `OrderNotFoundException` | 404 | Tracking code o ID non trovato |
| `PizzaNotFoundException` | 404 | Pizza ID non esiste |
| `InvalidStatusTransitionException` | 400 | Transizione di stato non valida |
| `OrderAlreadyInProgressException` | 409 | Si tenta di lavorare un ordine mentre un altro è già in corso |
| `MethodArgumentNotValidException` | 400 | Validazione input fallita (@Valid) |

---

## Dati Iniziali

`DataInitializer` (un `CommandLineRunner`) inserisce 5 pizze all'avvio se il DB è vuoto:

| Pizza | Descrizione | Prezzo |
|---|---|---|
| Margherita | Tomato sauce, mozzarella, fresh basil | €8.50 |
| Pepperoni | Tomato sauce, mozzarella, pepperoni | €10.00 |
| Quattro Formaggi | Mozzarella, gorgonzola, parmesan, fontina | €11.50 |
| Diavola | Tomato sauce, mozzarella, spicy salami, chili flakes | €10.50 |
| Capricciosa | Tomato sauce, mozzarella, ham, mushrooms, artichokes, olives | €12.00 |

---

## Test

Eseguire con: `./mvnw test` (ricorda `JAVA_HOME`)

### Test Unitari (Mockito)
- **OrderServiceTest** — Testa tutta la logica del service con mock dei repository
  - Creazione ordine, ordine non trovato, transizioni di stato valide/invalide, conflitto ordine in corso
- **PizzaServiceTest** — getAllPizzas, getPizzaById, pizza non trovata
- **OrderStatusTest** — Macchina a stati esaustiva: transizioni valide, invalide, backward, self, terminal

### Test di Integrazione
- **OrderRepositoryTest** (`@DataJpaTest`) — Query custom con `TestEntityManager`
- **OrderControllerTest** (`@WebMvcTest`) — MockMvc, testa endpoint HTTP reali
- **PizzaControllerTest** (`@WebMvcTest`) — MockMvc
- **PizzaioloControllerTest** (`@WebMvcTest`) — MockMvc, tutti gli scenari (200, 204, 400, 409)

---

## Endpoint API Completi

### Menu Pizze
```
GET  http://localhost:8080/api/pizzas          → Lista pizze
GET  http://localhost:8080/api/pizzas/{id}     → Dettaglio pizza
```

### Ordini (Cliente)
```
POST http://localhost:8080/api/orders                    → Crea ordine
GET  http://localhost:8080/api/orders/{trackingCode}     → Traccia ordine
```

### Gestione Ordini (Pizzaiolo)
```
GET  http://localhost:8080/api/pizzaiolo/orders              → Coda ordini FIFO
GET  http://localhost:8080/api/pizzaiolo/orders/current      → Ordine in lavorazione
PUT  http://localhost:8080/api/pizzaiolo/orders/{id}/status  → Aggiorna stato
```

### Altro
```
GET  http://localhost:8080/                    → Redirect a Swagger UI
GET  http://localhost:8080/swagger-ui.html     → Documentazione API interattiva
GET  http://localhost:8080/api-docs            → OpenAPI JSON
GET  http://localhost:8080/h2-console          → Console database
```

---

## Flusso Completo di un Ordine

```
1. Cliente consulta il menu
   GET /api/pizzas → lista delle pizze con ID

2. Cliente crea un ordine
   POST /api/orders
   Body: { "customerName": "Mario", "items": [{"pizzaId": "...", "quantity": 2}] }
   Risposta: { "trackingCode": "A1B2C3D4", "status": "PENDING" }

3. Cliente traccia il suo ordine
   GET /api/orders/A1B2C3D4
   Risposta: { "trackingCode": "A1B2C3D4", "status": "PENDING" }

4. Pizzaiolo vede la coda
   GET /api/pizzaiolo/orders
   Risposta: lista ordinata FIFO

5. Pizzaiolo prende in carico l'ordine
   PUT /api/pizzaiolo/orders/{orderId}/status
   Body: { "status": "IN_PROGRESS" }
   ⚠️ Se c'è già un ordine IN_PROGRESS → 409 Conflict

6. Pizzaiolo segna l'ordine come pronto
   PUT /api/pizzaiolo/orders/{orderId}/status
   Body: { "status": "READY" }

7. Cliente ritira la pizza
   PUT /api/pizzaiolo/orders/{orderId}/status
   Body: { "status": "PICKED_UP" }
   L'ordine scompare dalla coda attiva.
```
