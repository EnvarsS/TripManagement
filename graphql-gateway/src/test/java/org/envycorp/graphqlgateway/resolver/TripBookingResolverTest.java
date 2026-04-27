package org.envycorp.graphqlgateway.resolver;

import org.envycorp.graphqlgateway.client.BookingClient;
import org.envycorp.graphqlgateway.model.Booking;
import org.envycorp.graphqlgateway.model.Trip;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripBookingResolverTest {

    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private TripBookingResolver tripBookingResolver;

    @Test
    void getBookings_ReturnsBookings() {
        Trip trip = Trip.builder().id(1L).destination("Paris").build();
        List<Booking> bookings = List.of(
                Booking.builder().id(1L).status("CONFIRMED").build(),
                Booking.builder().id(2L).status("PENDING").build()
        );

        when(bookingClient.getBookingsByTripId(1L)).thenReturn(bookings);

        List<Booking> result = tripBookingResolver.getBookings(trip);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    void getBookings_ReturnsEmptyList() {
        Trip trip = Trip.builder().id(1L).destination("Paris").build();

        when(bookingClient.getBookingsByTripId(1L)).thenReturn(Collections.emptyList());

        List<Booking> result = tripBookingResolver.getBookings(trip);

        assertThat(result).isEmpty();
    }
}
