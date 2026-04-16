# Smart Campus Sensor Room Management API

## Overview
This project is a RESTful API built using JAX-RS (Jersey) and Maven, developed as part of the 5COSC022W Client-Server Architectures module at the University of Westminster. It simulates a backend system for the university's "Smart Campus" initiative, providing endpoints to manage Rooms and the Sensors deployed within them, as well as historical Sensor Readings.

All data is stored in-memory using `HashMap` and `ArrayList` structures. No database is used, in line with the brief's instructions.

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
| `/api/v1/sensors/{sensorId}/readings` | GET | Retrieve all readings for a sensor |
| `/api/v1/sensors/{sensorId}/readings` | POST | Submit a new reading for a sensor |

## Resource Hierarchy
Rooms are the top-level resource. Each Room can contain multiple Sensors identified by ID. Each Sensor maintains a list of historical `SensorReading` objects. Posting a new reading also updates the `currentValue` field on the parent `Sensor` to keep the latest value in sync.

## Error Handling
The API uses custom exception mappers to return structured JSON error responses for all failure scenarios:

- `409 Conflict` — attempting to delete a room that still has sensors
- `422 Unprocessable Entity` — registering a sensor with a `roomId` that does not exist
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

The examples below are written for **Windows Command Prompt**.

**Get API discovery info:**
```cmd
curl -X GET http://localhost:8080/JAX-RS_Coursework/api/v1
```

**Get all rooms:**
```cmd
curl -X GET http://localhost:8080/JAX-RS_Coursework/api/v1/rooms
```

**Create a room:**
```cmd
curl -X POST http://localhost:8080/JAX-RS_Coursework/api/v1/rooms -H "Content-Type: application/json" -d "{\"id\":\"SCI-101\",\"name\":\"Science Lab\",\"capacity\":30,\"sensorIds\":[]}"
```

**Register a sensor:**
```cmd
curl -X POST http://localhost:8080/JAX-RS_Coursework/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"TEMP-001\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":21.5,\"roomId\":\"SCI-101\"}"
```

**Get sensors filtered by type:**
```cmd
curl -X GET "http://localhost:8080/JAX-RS_Coursework/api/v1/sensors?type=Temperature"
```

**Post a sensor reading:**
```cmd
curl -X POST http://localhost:8080/JAX-RS_Coursework/api/v1/sensors/TEMP-001/readings -H "Content-Type: application/json" -d "{\"id\":\"R-001\",\"timestamp\":1712870000000,\"value\":22.8}"
```

**Delete a room (must have no sensors assigned):**
```cmd
curl -X DELETE http://localhost:8080/JAX-RS_Coursework/api/v1/rooms/ENG-201
```

## Conceptual Report

### Question 1.1
JAX-RS resource classes follow a per-request lifecycle, this means that a new instance is created for each incoming request, making them stateless. By default, the runtime does not treat it as a singleton, however, the lifecycle can be modified with an implementation-specific annotation like @Singleton to create a single instance for the entire application lifecycle. The per-request lifecycle prevents the need for synchronization of in-memory data within the resource class itself; this impacted the way I managed and synchronized my in-memory data structures as it justified my use of a shared static DataStore without synchronization. Each request gets its own resource instance but accesses the same in-memory data safely since the data structures are thread-safe or protected by the datastore logic.

### Question 1.2
The provision of Hypermedia is a key architectural constraint of REST. It dictates that responses should contain links that allow clients to discover available actions and transition between application states dynamically. This is a benefit for client developers over static documentation as it enables API evolution without client breakage, this means that servers can change URIs or add endpoints while keeping link names consistent whilst also reducing maintenance burden. My DiscoveryResource demonstrates this by providing two links to the client: /rooms and /sensors.

### Question 2.1
The implications of returning only IDs versus returning the full room objects are that returning full objects increases payload size but reduces client roundtrips, the IDs are lightweight but require extra fetches. If I run a GET request on /api/v1/rooms, full objects are efficient for small datasets but become wasteful as the list gets larger. I return full Room objects because it simplifies clients, but I do recognize that this could be an issue at larger scales. 

### Question 2.2
The DELETE operation is idempotent in my implementation. This means that calling it multiple times produces the same result because the resource is gone after the first time it’s called. This means that if a client mistakenly sends the exact same DELETE request for a room multiple times there would be no side effects or issues, they will simply get the same 404 error from the second time they try to call the DELETE operation onwards.

### Question 3.1
We use the @Consumes(MediaType.APPLICATION_JSON) annotation on the POST method to make sure that if the Content-Type header doesn’t match, JAX-RS returns a 415 Unsupported Media Type error. This ensures that only valid payloads are processed. If a client attempts to send data to /sensors or /rooms that isn’t JSON, they will receive the 415 error and the payload will be blocked.

### Question 3.2
I used @QueryParam because it allows for optional/filtering parameters like ?type=CO2. It supports multiple/optional values without making the URI explode. In contrast, @PathParam is for identification in the URI hierarchy like sensors/type/CO2, it forces hierarchy changes. This difference makes the query parameter approach superior for filtering and searching collections because it’s optional, bookmarkable, and extensible. I could do something like add ?status=ACTIVE later without changing the path structure. 

### Question 4.1
The main architectural benefit of the Sub-Resource locator pattern is that it returns instances for nested paths like /sensors/{id}/readings, delegating them to specialized classes. This helps manage complexity because it separates concerns, in this case: SensorResource handles sensors while readings stay isolated in their own class instead of one massive controller class which would be harder to navigate.

### Question 5.2
HTTP 422 is often considered more semantically accurate than a standard 404 because it is for valid syntax but with semantic errors, while 404 is just for when the resource path itself doesn’t exist.

### Question 5.4
The risk associated with exposing internal Java stack traces to external API consumers  is that it reveals the internal structure to attackers. They could gather package names, library versions, file paths, etc. All of this can allow them to plan targeted exploits against the app. 

### Question 5.5
It is advantageous to use JAX-RS filters, like ContainerRequestFilter and ContainerResponseFilter, for cross-cutting concerns like logging as it follows DRY (Don’t Repeat Yourself) principles. One filter covers the entire API instead of duplicating logging code everywhere.

#### _Click below to download a PDF version of the report._
[Download JAX-RS Conceptual Report PDF](./JAX-RS%20Conceptual%20Report.pdf)

## Academic Context
Built as a coursework project for **5COSC022W – Client-Server Architectures** at the University of Westminster.

## Licence
This project is for portfolio and educational purposes. Not licensed for redistribution.
