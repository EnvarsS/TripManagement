package com.kafka.gateway.controller;

//import com.example.gateway.client.*;
import com.kafka.gateway.client.BookingClient;
import com.kafka.gateway.client.TripClient;
import com.kafka.gateway.client.UserClient;
import com.kafka.gateway.dto.BookingDTO;
import com.kafka.gateway.dto.TripDTO;
import com.kafka.gateway.dto.UserDTO;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserGraphQLController {

    private final UserClient userClient;
    private final TripClient tripClient;
    private final BookingClient bookingClient;

    @QueryMapping
    public UserDTO user(@Argument Long id) {
        log.info("Fetching user with id: {}", id);
        return userClient.getUserById(id);
    }

    @SchemaMapping(typeName = "User", field = "trips")
    public List<TripDTO> getTrips(UserDTO user) {
        log.info("Fetching trips for user: {}", user.getId());
        return tripClient.getTripsByUserId(user.getId());
    }

    @SchemaMapping(typeName = "Trip", field = "bookings")
    public List<BookingDTO> getBookings(TripDTO trip) {
        log.info("Fetching bookings for trip: {}", trip.getId());
        return bookingClient.getBookingsByTripId(trip.getId());
    }
}
