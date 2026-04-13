# Smart Campus Sensor Room Management API

## Overview
This project is a RESTful API built using JAX-RS (Jersey) and Maven, developed as part of the 5COSC022W Client-Server Architectures module at the University of Westminster. It simulates a backend system for the university's "Smart Campus" initiative, providing endpoints to manage Rooms and the Sensors deployed within them, as well as historical Sensor Readings.

All data is stored in-memory using `HashMap` and `ArrayList` structures. No database is used, in-line with the briefs instructions.

## API Design

| Endpoint | Method | Description |
|---|---|---|
| `/api/v1` | GET | Discovery endpoint — returns API metadata and links to primary resources |
| `/api/v1/rooms` | GET | Retrieve all rooms |
| `/api/v1/rooms` | POST | Create a new room |
| `/api/v1/rooms/{roomId}` | GET | Retrieve a specific room by ID |
| `/api/v1/rooms/{roomId}` | DELETE | Delete a room (blocked if sensors are still assigned) |
| `/api/v1/sensors` | GET | Retrieve all sensors, with optional `?type=` filter |
| `/api/v1/sensors` | POST | Register a new sensor (roomId must exist) |
| `/api/v1/sensors/{sensorId}` | DELETE | Delete a sensor by ID |
| `/api/v1/sensors/{sensorId}/readings` | GET | Retrieve all readings for a sensor |
| `/api/v1/sensors/{sensorId}/readings` | POST | Submit a new reading for a sensor |

### Resource Hierarchy
Rooms are the top-level resource. Each Room can contain multiple Sensors identified by ID. Each Sensor maintains a list of historical SensorReadings. Posting a new reading also updates the `currentValue` field on the parent Sensor.

### Error Handling
The API uses custom exception mappers to return structured JSON error responses for all failure scenarios:
- `409 Conflict` — attempting to delete a room that still has sensors
- `422 Unprocessable Entity` — registering a sensor with a roomId that does not exist
- `403 Forbidden` — posting a reading to a sensor with status `MAINTENANCE`
- `500 Internal Server Error` — catch-all for any unexpected runtime errors

All requests and responses are logged via a JAX-RS filter using `java.util.logging.Logger`.

## How to Build and Run

1. Open the project in NetBeans.
2. Right-click the project → **Clean and Build**.
3. Right-click the project → **Run** (deploys to the embedded GlassFish/Tomcat server).
4. The API will be available at:
   - `http://localhost:8080/JAX-RS_Coursework/api/v1`

## Sample curl Commands

**Get API discovery info:**
```bash
curl -X GET http://localhost:8080/JAX-RS_Coursework/api/v1
```

**Get all rooms:**
```bash
curl -X GET http://localhost:8080/JAX-RS_Coursework/api/v1/rooms
```

**Create a room:**
```bash
curl -X POST http://localhost:8080/JAX-RS_Coursework/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"SCI-101\",\"name\":\"Science Lab\",\"capacity\":30,\"sensorIds\":[]}"
```

**Register a sensor:**
```bash
curl -X POST http://localhost:8080/JAX-RS_Coursework/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"TEMP-001\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":21.5,\"roomId\":\"SCI-101\"}"
```

**Get sensors filtered by type:**
```bash
curl -X GET "http://localhost:8080/JAX-RS_Coursework/api/v1/sensors?type=Temperature"
```

**Post a sensor reading:**
```bash
curl -X POST http://localhost:8080/JAX-RS_Coursework/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"R-001\",\"timestamp\":1712870000000,\"value\":22.8}"
```

**Delete a sensor:**
```bash
curl -X DELETE http://localhost:8080/JAX-RS_Coursework/api/v1/sensors/TEMP-001
```

**Delete a room (must have no sensors assigned):**
```bash
curl -X DELETE http://localhost:8080/JAX-RS_Coursework/api/v1/rooms/SCI-101
```

## 🎓 Academic Context
Built as a coursework project for **5COSC022W - Client-Server Architectures**, at the University of Westminster.

---

## 📄 Licence

This project is for portfolio and educational purposes. Not licensed for redistribution.
