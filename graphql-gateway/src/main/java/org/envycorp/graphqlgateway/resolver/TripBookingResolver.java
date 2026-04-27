package org.envycorp.graphqlgateway.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.graphqlgateway.client.BookingClient;
import org.envycorp.graphqlgateway.model.Booking;
import org.envycorp.graphqlgateway.model.Trip;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TripBookingResolver {

    private final BookingClient bookingClient;

    @SchemaMapping(typeName = "Trip", field = "bookings")
    public List<Booking> getBookings(Trip trip) {
        log.info("[GraphQL] Resolving bookings for trip id={}", trip.getId());
        return bookingClient.getBookingsByTripId(trip.getId());
    }
}
