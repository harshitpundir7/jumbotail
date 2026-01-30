# Shipping Charge Estimator

A production-grade B2B e-commerce shipping charge estimation API built with Spring Boot 3.2.x. Designed for Kirana stores to calculate shipping costs based on distance, transport mode, and delivery speed.

## 🚀 Features

- **Nearest Warehouse Lookup** - Find closest warehouse to seller location using Haversine formula
- **Shipping Charge Calculation** - Calculate costs based on distance, weight, and delivery speed
- **Transport Mode Selection** - Automatic selection (Aeroplane/Truck/Mini Van) based on distance
- **Caching** - Caffeine-based caching for improved performance
- **OpenAPI Documentation** - Swagger UI for API exploration
- **Comprehensive Testing** - Unit and integration tests with >80% coverage
- **Production Ready** - Health checks, metrics, and proper exception handling

## 📋 Prerequisites

- Java 17+
- Maven 3.8+

## 🛠️ Quick Start

### 1. Clone and Build

```bash
cd jumbotail-java-backend
./mvnw clean install
```

### 2. Run the Application

```bash
./mvnw spring-boot:run
```

The application starts at `http://localhost:8080`

### 3. Access Swagger UI

Open `http://localhost:8080/swagger-ui.html` for interactive API documentation.
<!-- Swagger UI: http://localhost:8080/swagger-ui.html
H2 Console: http://localhost:8080/h2-console
Health Check: http://localhost:8080/actuator/health -->

## 📡 API Endpoints

### 1. Get Nearest Warehouse

```bash
GET /api/v1/warehouse/nearest?sellerId=1&productId=1
```

**Response:**
```json
{
  "warehouseId": 1,
  "warehouseCode": "BLR_WH_01",
  "warehouseName": "Bangalore Central Warehouse",
  "warehouseLocation": { "lat": 12.9716, "lng": 77.5946 },
  "distanceKm": 45.5
}
```

### 2. Get Shipping Charge

```bash
GET /api/v1/shipping-charge?warehouseId=1&customerId=1&deliverySpeed=STANDARD
```

**Response:**
```json
{
  "shippingCharge": 150.00,
  "transportMode": "TRUCK",
  "deliverySpeed": "STANDARD",
  "distanceKm": 245.5,
  "weightKg": 1.0,
  "currency": "INR"
}
```

### 3. Calculate Complete Shipping

```bash
POST /api/v1/shipping-charge/calculate
Content-Type: application/json

{
  "sellerId": 1,
  "customerId": 1,
  "productId": 1,
  "deliverySpeed": "EXPRESS"
}
```

**Response:**
```json
{
  "shippingCharge": 180.00,
  "nearestWarehouse": {
    "warehouseId": 1,
    "warehouseCode": "BLR_WH_01",
    "warehouseLocation": { "lat": 12.9716, "lng": 77.5946 }
  },
  "transportMode": "AEROPLANE",
  "deliverySpeed": "EXPRESS",
  "distanceKm": 520.5,
  "weightKg": 5.0,
  "currency": "INR"
}
```

## 💰 Pricing Logic

### Transport Modes (based on distance)

| Mode      | Distance    | Rate         |
|-----------|-------------|--------------|
| Aeroplane | 500+ km     | ₹1/km/kg     |
| Truck     | 100-500 km  | ₹2/km/kg     |
| Mini Van  | 0-100 km    | ₹3/km/kg     |

### Delivery Speeds

| Speed    | Charges                              |
|----------|--------------------------------------|
| Standard | ₹10 base + transport charge          |
| Express  | ₹10 base + ₹1.2/kg + transport charge |

## 🧪 Running Tests

```bash
# Run all tests
./mvnw test

# Run with coverage report
./mvnw test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

## 📊 Health Check & Metrics

```bash
# Health check
curl http://localhost:8080/actuator/health

# Application info
curl http://localhost:8080/actuator/info

# Cache statistics
curl http://localhost:8080/actuator/caches
```

## 🏗️ Project Structure

```
src/main/java/com/jumbotail/shipping/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/             # Request/Response DTOs
├── entity/          # JPA entities
├── enums/           # TransportMode, DeliverySpeed
├── exception/       # Exception handling
├── repository/      # Spring Data repositories
└── service/         # Business logic
```

## 🔧 Configuration

Key configuration in `application.yml`:

```yaml
shipping:
  transport:
    aeroplane:
      rate-per-km-per-kg: 1.0
      min-distance-km: 500.0
    truck:
      rate-per-km-per-kg: 2.0
      min-distance-km: 100.0
    mini-van:
      rate-per-km-per-kg: 3.0
```

## 📝 Sample Data

The application initializes with sample data:
- 5 Warehouses (Bangalore, Mumbai, Delhi, Chennai, Kolkata)
- 5 Sellers across India
- 5 Customers (Kirana stores)
- 10 Products with varying weights

## 🔒 Error Handling

All errors return a consistent structure:

```json
{
  "timestamp": "2024-01-29T18:05:00",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with id: 999",
  "path": "/api/v1/shipping-charge",
  "traceId": "a1b2c3d4"
}
```

## 📄 License

Proprietary - Jumbotail

---

Built with ❤️ for Jumbotail Engineering Interview
