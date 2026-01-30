# 🚚 Jumbotail Shipping Charge Estimator

Production-grade B2B shipping charge API built with **Spring Boot 3.2**. Calculates shipping costs based on distance, transport mode, and delivery speed for Kirana stores.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

## 🌐 Live Demo

| Environment | URL |
|------------|-----|
| **Production** | [https://jumbotail.harshitpundir.tech](https://jumbotail.harshitpundir.tech) |
| **Swagger UI** | [/swagger-ui.html](https://jumbotail.harshitpundir.tech/swagger-ui.html) |
| **Health Check** | [/actuator/health](https://jumbotail.harshitpundir.tech/actuator/health) |

---

## 🐳 Quick Start (Docker)

```bash
git clone https://github.com/your-username/jumbotail.git
cd jumbotail
docker compose up
```

App runs at **http://localhost:8080** — that's it!

### Docker Commands
```bash
docker compose up           # Start
docker compose down         # Stop
docker compose up --build   # Rebuild after changes
docker compose up -d        # Run in background
```

---

## 🛠️ Local Development

```bash
./mvnw spring-boot:run
```

Requires: Java 17+

---

## 🏗️ Architecture

```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐
│   Seller    │────▶│  Warehouse   │────▶│  Customer   │
│  (Pickup)   │     │   (Hub)      │     │  (Delivery) │
└─────────────┘     └──────────────┘     └─────────────┘
       │                   │                    │
       └───────── Distance-based pricing ──────┘
```

**Flow:**
1. Seller drops product at nearest warehouse (Haversine distance)
2. Warehouse ships to customer
3. Transport mode auto-selected based on distance
4. Price = Base + (Distance × Weight × Rate) + Delivery charges

---

## 📁 Project Structure

```
src/main/java/com/jumbotail/shipping/
├── config/          # Cache, OpenAPI configuration
├── controller/      # REST endpoints
├── dto/             # Request/Response objects
├── entity/          # JPA entities (Seller, Customer, Product, Warehouse)
├── enums/           # TransportMode, DeliverySpeed
├── exception/       # Global error handling
├── repository/      # Spring Data JPA
└── service/         # Business logic
```

---

## 🎯 Features

| Feature | Description |
|---------|-------------|
| **Interactive UI** | Landing page with live API testing |
| **3 API Tabs** | Complete Flow, Direct Shipping, Find Warehouse |
| **Data Tables** | Real-time view of Warehouses, Sellers, Customers |
| **Swagger Docs** | Full OpenAPI 3.0 documentation |
| **Caffeine Cache** | Sub-millisecond response times |
| **Health Checks** | Actuator endpoints for monitoring |

---

## 📡 API Endpoints

### Core APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/shipping-charge/calculate` | Full shipping calculation |
| `GET` | `/api/v1/shipping-charge` | Direct warehouse→customer quote |
| `GET` | `/api/v1/warehouse/nearest` | Find nearest warehouse |

### Example: Calculate Shipping

```bash
curl -X POST http://localhost:8080/api/v1/shipping-charge/calculate \
  -H "Content-Type: application/json" \
  -d '{"sellerId": 1, "customerId": 1, "productId": 1, "deliverySpeed": "EXPRESS"}'
```

```json
{
  "shippingCharge": 180.00,
  "transportMode": "AEROPLANE",
  "deliverySpeed": "EXPRESS",
  "distanceKm": 520.5,
  "weightKg": 5.0,
  "currency": "INR"
}
```

---

## 💰 Pricing Logic

### Transport Modes
| Mode | Distance | Rate |
|------|----------|------|
| ✈️ Aeroplane | 500+ km | ₹1/km/kg |
| 🚛 Truck | 100-500 km | ₹2/km/kg |
| 🚐 Mini Van | 0-100 km | ₹3/km/kg |

### Delivery Speeds
| Speed | Formula |
|-------|---------|
| Standard | ₹10 base + transport |
| Express | ₹10 base + ₹1.2/kg + transport |

---

## 🧪 Testing

```bash
./mvnw test                    # Run tests
./mvnw test jacoco:report      # Generate coverage
```

---

## � Sample Data

Pre-loaded with:
- 5 Warehouses (Bangalore, Mumbai, Delhi, Chennai, Kolkata)
- 5 Sellers across India
- 5 Kirana store customers
- 10 Products with varying weights

---

## 🔧 Configuration

Key settings in `application.yml`:

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

---

## � Useful Links

- **Swagger UI**: `/swagger-ui.html` - Interactive API docs
- **H2 Console**: `/h2-console` - Database explorer (dev)
- **Health**: `/actuator/health` - App health status
- **Metrics**: `/actuator/metrics` - Performance metrics

---

## 📄 License

Proprietary - Jumbotail

---

Built with ❤️ for Jumbotail Engineering
