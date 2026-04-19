# Booking Service

`booking-service` is a Spring Boot microservice for managing trip bookings.

It provides a GraphQL API for:
- creating a booking
- fetching all bookings
- fetching a booking by id
- confirming a booking
- canceling a booking

It also publishes Kafka events for:
- `booking.created`
- `booking.confirmed`

## Tech Stack

- Java 21
- Spring Boot 3.3.5
- Spring Data JPA
- Spring for GraphQL
- Spring for Apache Kafka
- MySQL
- Gradle

## Project Structure

- `entity` - JPA entities
- `repository` - Spring Data repositories
- `service` - business logic
- `graphql` - GraphQL query and mutation handlers
- `dto` - request and response models
- `mapper` - mapping between DTOs and entities
- `kafka/producer` - Kafka producers
- `exception` - custom exceptions and GraphQL error handling

## Database

The service uses MySQL and works with schema `trip_db`.

Docker initialization script:
- [db/init-schemas.sql](/C:/Users/kluki/IdeaProjects/TripManagement/db/init-schemas.sql)

Application properties:
- [application.properties](/C:/Users/kluki/IdeaProjects/TripManagement/booking-service/src/main/resources/application.properties)

## Run Locally

1. Start infrastructure:

```powershell
docker compose up -d
```

2. Start the service:

```powershell
.\gradlew.bat :booking-service:bootRun
```

The service starts on:
- `http://localhost:8089`

GraphQL endpoint:
- `http://localhost:8089/graphql`

## GraphQL Operations

Schema file:
- [schemaBooking.graphqls](/C:/Users/kluki/IdeaProjects/TripManagement/booking-service/src/main/resources/graphql/schemaBooking.graphqls)

### Create Booking

```graphql
mutation {
  createBooking(request: { tripId: 1, userId: 100 }) {
    id
    tripId
    userId
    status
    createdAt
  }
}
```

### Get All Bookings

```graphql
query {
  getAllBookings {
    id
    tripId
    userId
    status
    createdAt
  }
}
```

### Get Booking By Id

```graphql
query {
  getBookingById(id: 1) {
    id
    tripId
    userId
    status
    createdAt
  }
}
```

### Confirm Booking

```graphql
mutation {
  confirmBooking(id: 1) {
    id
    tripId
    userId
    status
    createdAt
  }
}
```

### Cancel Booking

```graphql
mutation {
  cancelBooking(id: 1) {
    id
    tripId
    userId
    status
    createdAt
  }
}
```

## Kafka Events

Topics used by the service:
- `booking.created`
- `booking.confirmed`

Example `booking.created` payload:

```json
{
  "bookingId": 1,
  "tripId": 1,
  "userId": 100,
  "status": "CREATED",
  "createdAt": "2026-04-19T18:05:54.1113289"
}
```

Example `booking.confirmed` payload:

```json
{
  "bookingId": 1,
  "tripId": 1,
  "userId": 100,
  "status": "CONFIRMED",
  "confirmedAt": "2026-04-19T18:05:54.1113289"
}
```

You can inspect topics manually:

```powershell
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --list
docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic booking.created --from-beginning
docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic booking.confirmed --from-beginning
```

## Testing

Unit tests:
- [BookingServiceTest.java](/C:/Users/kluki/IdeaProjects/TripManagement/booking-service/src/test/java/com/example/bookingservice/service/BookingServiceTest.java)

Integration tests:
- [BookingServiceIntegrationTest.java](/C:/Users/kluki/IdeaProjects/TripManagement/booking-service/src/test/java/com/example/bookingservice/service/BookingServiceIntegrationTest.java)

Run tests:

```powershell
.\gradlew.bat :booking-service:test
```

## Notes

- The service uses GraphQL, not REST controllers.
- Booking not found errors are handled via GraphQL exception handling.
- Kafka events are shared through `common-module`.
