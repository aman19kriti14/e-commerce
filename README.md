# E-commerce Order Processing System

A RESTful backend API built with **Spring Boot 4.x** for managing e-commerce orders — supporting creation, tracking, status updates, and cancellation with an automated background scheduler.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.0.6 |
| Language | Java 17 |
| Database | PostgreSQL |
| ORM | Spring Data JPA + Hibernate |
| Scheduler | Spring `@Scheduled` |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Build Tool | Maven |

---

## Project Structure

```
src/main/java/com/ecommerce/
├── controller/
│   └── OrderController.java
├── service/
│   ├── OrderService.java
│   └── OrderServiceImpl.java
├── repository/
│   ├── OrderRepository.java
│   └── OrderItemRepository.java
├── model/
│   ├── Order.java
│   ├── OrderItem.java
│   └── OrderStatus.java
├── dto/
│   ├── CreateOrderRequest.java
│   ├── OrderItemRequest.java
│   ├── OrderResponse.java
│   └── OrderItemResponse.java
├── exception/
│   ├── OrderNotFoundException.java
│   ├── OrderCancellationException.java
│   └── GlobalExceptionHandler.java
├── scheduler/
│   └── OrderScheduler.java
└── EcommerceApplication.java
```

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 14+

### Step 1 — Clone the repository
```bash
git clone https://github.com/aman19kriti14/e-commerce.git
cd e-commerce



### Step 2 — Create the database
```bash
psql -U postgres
```
```sql
CREATE DATABASE ecommerce_db;
\q
```

### Step 3 — Configure database credentials
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Step 4 — Run the application
```bash
mvn spring-boot:run
```

### Step 5 — Verify tables are created
On startup Hibernate automatically creates:
```
orders
order_items
```

### Step 6 — Access Swagger UI
```
http://localhost:8080/swagger-ui.html
```

---

## API Endpoints

### Base URL
```
http://localhost:8080/api/orders
```

---

### 1. Create Order
```
POST /api/orders
Content-Type: application/json
```

**Request Body:**
```json
{
  "customerName": "John Doe",
  "items": [
    {
      "productName": "Laptop",
      "quantity": 1,
      "price": 999.99
    },
    {
      "productName": "Mouse",
      "quantity": 2,
      "price": 29.99
    }
  ]
}
```

**Response: 201 Created**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "customerName": "John Doe",
  "status": "PENDING",
  "items": [
    {
      "id": "660e8400-e29b-41d4-a716-446655440000",
      "productName": "Laptop",
      "quantity": 1,
      "price": 999.99
    }
  ],
  "createdAt": "2026-05-11T10:00:00",
  "updatedAt": "2026-05-11T10:00:00"
}
```

---

### 2. Get Order by ID
```
GET /api/orders/{id}
```

**Response: 200 OK**

**Error: 404 Not Found**
```json
{
  "timestamp": "2026-05-11T10:00:00",
  "status": 404,
  "error": "Order not found with id: {id}"
}
```

---

### 3. List All Orders
```
GET /api/orders
GET /api/orders?status=PENDING
```

Supported status values: `PENDING` `PROCESSING` `SHIPPED` `DELIVERED` `CANCELLED`

**Response: 200 OK** — returns array of orders

---

### 4. Update Order Status
```
PATCH /api/orders/{id}/status?status=SHIPPED
```

**Response: 200 OK** — returns updated order

**Error: 400 Bad Request** (if order is CANCELLED)
```json
{
  "timestamp": "2026-05-11T10:00:00",
  "status": 400,
  "error": "Cannot update status of a cancelled order"
}
```

---

### 5. Cancel Order
```
DELETE /api/orders/{id}
```

**Response: 204 No Content**

**Error: 400 Bad Request** (if order is not PENDING)
```json
{
  "timestamp": "2026-05-11T10:00:00",
  "status": 400,
  "error": "Order can only be cancelled when in PENDING status. Current status: SHIPPED"
}
```

---

## Order Status Flow

```
PENDING ──(auto every 5 min)──► PROCESSING ──► SHIPPED ──► DELIVERED
   │
   └──(manual cancel)──► CANCELLED
```

---

## Background Scheduler

A scheduled job runs **every 5 minutes** automatically and promotes all `PENDING` orders to `PROCESSING` status. No manual trigger needed.

**Console output:**
```
Scheduler running — checking for PENDING orders...
Found 3 PENDING order(s). Updating to PROCESSING...
Successfully updated 3 order(s) to PROCESSING.
```

---

## Validation Rules

| Field | Rule |
|---|---|
| `customerName` | Must not be blank |
| `items` | Must have at least one item |
| `productName` | Must not be blank |
| `quantity` | Must be at least 1 |
| `price` | Must be greater than 0.00 |

**Validation Error: 400 Bad Request**
```json
{
  "timestamp": "2026-05-11T10:00:00",
  "status": 400,
  "error": "Validation failed",
  "fieldErrors": {
    "customerName": "Customer name must not be blank"
  }
}
```

---

## Postman Collection

A Postman collection is included in the repository root:
```
ecommerce-orders.postman_collection.json
```

**Import steps:**
```
1. Open Postman
2. Click "Import"
3. Select ecommerce-orders.postman_collection.json
4. All endpoints are ready to use at localhost:8080
```
