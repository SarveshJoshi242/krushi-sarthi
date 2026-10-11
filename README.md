# 🌾 Krushi-Adhaar — Smart Agricultural Management & Marketplace Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Android](https://img.shields.io/badge/Android-Jetpack%20Compose-green.svg)](https://developer.android.com/jetpack/compose)
[![PostgreSQL](https://img.shields.io/badge/Database-Neon%20Cloud%20Postgres-blue.svg)](https://neon.tech/)
[![FastAPI](https://img.shields.io/badge/FastAPI-Python%203.9-teal.svg)](https://fastapi.tiangolo.com/)
[![Gemini](https://img.shields.io/badge/AI-Google%20Gemini%202.0%20Flash-orange.svg)](https://deepmind.google/technologies/gemini/)
[![TensorFlow](https://img.shields.io/badge/ML-TensorFlow%20%2F%20TFLite-red.svg)](https://www.tensorflow.org/)

---

## 📌 Project Overview
**Krushi-Adhaar** is an end-to-end smart agriculture ecosystem designed to empower farmers and agricultural buyers with real-time intelligence, decentralized commerce, and automated farm operations. The platform integrates:
1. **Real Cloud Database (Neon PostgreSQL)** with 17 automated Flyway migrations ensuring data persistence and integrity.
2. **AI-Powered Disease Diagnosis** using Deep Learning (TensorFlow) combined with generative crop health advisory (Google Gemini 2.0 Flash) to deliver instant medicine recommendations, cost estimates, and organic remedies.
3. **Live Geolocation & Distance Calculation** utilizing the mathematical Haversine formula to compute exact field-to-buyer distances for logistics and radius filtering.
4. **Real-Time Peer-to-Peer Chat** powered by Spring WebSocket STOMP messaging for live negotiation between farmers and buyers.
5. **Live Weather & Government Schemes Integration** pulling live forecasts via OpenWeatherMap with smart 30-minute caching and localized agricultural risk advisories.
6. **Multilingual Voice Assistant (Hindi, Marathi, English)** featuring animated voice input, quick prompt chips, and localized Text-To-Speech (TTS) with full UI language toggling.
7. **Live APMC Mandi Price Tracker & Trends** providing real-time mandi prices, MSP benchmark comparisons, 7-day trend indicators, and distance tracking.
8. **Soil Health Card & NPK Fertilizer Calculator** calculating exact 50kg bags of Urea, DAP, and MOP per acre based on soil NPK/pH tests, along with lime/gypsum soil amendments and a 3-step application schedule.
9. **Smart Irrigation & Crop Activity Calendar** offering stage-by-stage crop tracking, weather-delay warnings for rain, water volume guidance, and an interactive task checklist.
10. **One-Click PDF Farm Health & Revenue Report** compiling a professional printable A4 report generated natively on Android with secure sharing via Android FileProvider.

---

## 🏗️ Architecture & Technology Stack

```
                              ┌────────────────────────────────────────┐
                              │      Android Mobile Application        │
                              │   (Kotlin, Jetpack Compose, MVVM)      │
                              └───────────────────┬────────────────────┘
                                                  │
                                   HTTPS REST & WSS STOMP
                                                  │
                                                  ▼
                              ┌────────────────────────────────────────┐
                              │       Spring Boot Backend API          │
                              │       (Java 17 / Gradle 8.7)           │
                              └───────┬────────────────────────┬───────┘
                                      │                        │
                     Flyway SQL / JDBC│                        │ HTTP REST
                                      ▼                        ▼
      ┌───────────────────────────────────────────┐  ┌───────────────────────────────────┐
      │          Neon PostgreSQL Cloud            │  │     FastAPI ML Microservice       │
      │  (Users, Farms, Listings, Chats, Cache)   │  │ (TensorFlow 2.x + Gemini 2.0 API) │
      └───────────────────────────────────────────┘  └─────────────────┬─────────────────┘
                                                                       │
                                                       OpenWeatherMap & Gemini Cloud
```

| Layer | Technologies & Tools |
|---|---|
| **Mobile Client** | Android (Min SDK 24, Target SDK 34), Jetpack Compose, Kotlin Coroutines & Flow, Room SQLite, Retrofit 2, OkHttp 3, EncryptedSharedPreferences, TensorFlow Lite |
| **Backend Core** | Spring Boot 3.2.5, Java 17, Spring Data JPA / Hibernate, Spring Security (Stateless JWT), Spring WebSocket (STOMP), Flyway DB, HikariCP |
| **Database** | Neon Cloud Serverless PostgreSQL (`neondb`), SSL required |
| **ML Microservice** | Python 3.9, FastAPI, Uvicorn, TensorFlow, Google Gemini 2.0 Flash, PIL, Pandas, NumPy |
| **External APIs** | OpenWeatherMap API, Google Gemini AI API |

---

## 🚀 One-Click Execution (Quick Start)

### Option A: Run Everything at Once
Simply double-click:
```bat
START_ALL_SERVICES.bat
```
This automatically launches:
* **ML Microservice** on `http://localhost:8000` (FastAPI Swagger docs at `/docs`)
* **Spring Boot Backend** on `http://localhost:8080` (Health probe at `/api/health`)

### Option B: Run Services Individually

#### 1. Backend Service (Spring Boot)
```cmd
cd krushi-adhhar\backend
.\gradlew.bat bootRun
```
*Port: 8080*

#### 2. ML Service (FastAPI)
```cmd
cd krushi-adhhar\ml_service
run.bat
```
*Port: 8000*

#### 3. Android Client App
- Open `krushi-adhhar\android` in **Android Studio** and click **Run**.
- Or install the pre-compiled APK directly:
  ```cmd
  adb install krushi-adhhar\android\app\build\outputs\apk\debug\app-debug.apk
  ```

---

## 🗄️ Database Schema & Flyway Migrations

The database is hosted on **Neon PostgreSQL** and managed through **Flyway versioned migrations**:

| Migration | Description |
|---|---|
| `V1` | Initial foundation verification baseline |
| `V2` | User identity (`users`), roles (`roles`), and refresh tokens (`refresh_tokens`) |
| `V3` | Farm domain (`farms`, `fields`, `crop_cycles`, `crop_activities`, `crop_expenses`, `harvests`) |
| `V4` | Disease diagnostic domain (`disease_scans`, `disease_results`) |
| `V5` | Marketplace domain (`harvest_inventory`, `marketplace_listings`, `orders`, `order_items`, `audit_logs`) |
| `V6` | Notification domain (`notifications`, `user_devices`) |
| `V7` | Payment domain (`payments` with idempotency validation) |
| `V8` | Order expiration handling (`payment_deadline` index) |
| `V9` | Order shipping addresses (`order_shipping_addresses`) |
| `V10` | Webhook idempotency tracking (`webhook_events`) |
| `V11` | User Aadhaar number and account recovery keys |
| `V12` | Disease prescription medicines and cost estimation columns |
| `V13` | Geolocation coordinates (`latitude`, `longitude`, `address`, `city`, `state`, `pincode`) on farms and listings |
| `V14` | Real-time chat system (`chat_rooms`, `chat_messages` with unread counts) |
| `V15` | Smart 30-minute weather cache (`weather_cache`) |
| `V16` | Seeded Indian government agricultural schemes (PM-KISAN, PMFBY, Soil Health Card, PM-KUSUM, eNAM, KCC, etc.) |
| `V17` | APMC Mandi prices tracking (`mandi_prices`) and Soil Health NPK records (`soil_health_records`) |

---

## 📡 API Reference & Endpoints

### 🔐 Authentication (`/api/v1/auth`)
- `POST /api/v1/auth/register` — Register a new farmer or buyer
- `POST /api/v1/auth/login` — Login and receive JWT access & refresh tokens
- `POST /api/v1/auth/refresh` — Refresh expired JWT access token

### 🌿 Farm & Field Management (`/api/farms`)
- `POST /api/farms` — Create a new farm with GPS coordinates
- `GET /api/farms` — List farms owned by current user
- `GET /api/farms/{id}` — Fetch detailed farm information
- `PUT /api/farms/{id}` — Update farm details and boundaries
- `DELETE /api/farms/{id}` — Archive a farm

### 📍 Geolocation & Logistics (`/api/location`)
- `GET /api/location/distance?lat1=...&lon1=...&lat2=...&lon2=...` — Calculate exact distance in kilometers using the Haversine formula

### 💬 Real-Time Chat & Messaging (`/api/chat` & `/ws`)
- `POST /api/chat/rooms` — Create or retrieve an active buyer-seller negotiation room
- `GET /api/chat/rooms` — List all conversations with unread badge counts
- `GET /api/chat/rooms/{roomId}/messages` — Get chronological chat history and mark messages as read
- `POST /api/chat/rooms/{roomId}/messages` — Post a new message via REST
- `WS /ws` (STOMP) — Real-time bidirectional WebSocket connection
  - Send: `/app/chat/{roomId}`
  - Subscribe: `/topic/chat/{roomId}`

### 🌦️ Weather & Agro-Advisories (`/api/weather`)
- `GET /api/weather/farm/{farmId}` — Get live weather and agronomic risk warnings for a farm
- `GET /api/weather/coordinates?lat=...&lon=...` — Query live weather by GPS coordinates

### 🌾 Farm Intelligence (`/api/v1/farm-intelligence`)
- `POST /api/v1/farm-intelligence/voice-assistant` — Multilingual voice prompt query (Hindi, Marathi, English)
- `GET /api/v1/farm-intelligence/mandi-prices` — Live APMC Mandi prices, MSP comparison, and 7-day price trends
- `POST /api/v1/farm-intelligence/soil-health` — Soil Health Card analyzer and Urea/DAP/MOP NPK fertilizer calculator
- `GET /api/v1/farm-intelligence/irrigation-calendar` — Smart stage-by-stage irrigation schedule with rain-delay warnings

### 🔬 Disease Diagnosis & ML (`/predict`)
- `POST http://localhost:8000/predict` — Multipart image upload for TensorFlow inference + Gemini advisory
- `POST http://localhost:8000/voice-assistant` — Microservice endpoint for multilingual voice queries
- `GET http://localhost:8000/mandi-prices` — APMC commodity market pricing
- `POST http://localhost:8000/soil-health` — Laboratory soil health NPK calculation engine
- `GET http://localhost:8000/irrigation-calendar` — Crop phenology water scheduling
- `GET http://localhost:8000/recommendations` — AI crop recommendations based on weather and season
- `GET http://localhost:8000/schemes` — Current government schemes and subsidies
- `GET http://localhost:8000/expenses` — Dynamic budget breakdown per acre
- `GET http://localhost:8000/health` — ML engine health status

---

## 📦 Generated Pre-Built Artifacts
For immediate evaluation and demonstration:
* **Android APK**: `krushi-adhhar/android/app/build/outputs/apk/debug/app-debug.apk` (33.5 MB)
* **Backend Executable Fat JAR**: `krushi-adhhar/backend/build/libs/backend-0.0.1-SNAPSHOT.jar` (55.9 MB)

---

## 👥 Contributors & Identity
- **Project**: Krushi-Adhaar (Crop Management & Agricultural Marketplace)
- **Year**: 2026
- **Status**: Production & Submission Ready ✅
