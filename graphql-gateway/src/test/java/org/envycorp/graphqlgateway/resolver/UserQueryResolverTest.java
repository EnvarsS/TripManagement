package org.envycorp.graphqlgateway.resolver;

import org.envycorp.graphqlgateway.client.BookingClient;
import org.envycorp.graphqlgateway.client.TripClient;
import org.envycorp.graphqlgateway.client.UserClient;
import org.envycorp.graphqlgateway.model.Booking;
import org.envycorp.graphqlgateway.model.Trip;
import org.envycorp.graphqlgateway.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@GraphQlTest({UserQueryResolver.class, UserTripResolver.class, TripBookingResolver.class})
class UserQueryResolverTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private UserClient userClient;

    @MockBean
    private TripClient tripClient;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void user_ReturnsUser() {
        User mockUser = User.builder()
                .id(1L)
                .name("John Doe")
                .build();

        when(userClient.getUserById(eq(1L))).thenReturn(mockUser);

        graphQlTester.document("""
                query {
                    user(id: 1) {
                        id
                        name
                    }
                }
                """)
                .execute()
                .path("user.id").entity(String.class).isEqualTo("1")
                .path("user.name").entity(String.class).isEqualTo("John Doe");
    }

    @Test
    void user_NotFound_ReturnsNull() {
        when(userClient.getUserById(eq(999L))).thenReturn(null);

        graphQlTester.document("""
                query {
                    user(id: 999) {
                        id
                        name
                    }
                }
                """)
                .execute()
                .path("user").valueIsNull();
    }

    @Test
    void user_WithTrips_ReturnsNestedData() {
        User mockUser = User.builder()
                .id(1L)
                .name("John Doe")
                .build();

        List<Trip> mockTrips = List.of(
                Trip.builder().id(1L).destination("Paris").build(),
                Trip.builder().id(2L).destination("London").build()
        );

        when(userClient.getUserById(eq(1L))).thenReturn(mockUser);
        when(tripClient.getTripsByUserId(eq(1L))).thenReturn(mockTrips);

        graphQlTester.document("""
                query {
                    user(id: 1) {
                        id
                        name
                        trips {
                            id
                            destination
                        }
                    }
                }
                """)
                .execute()
                .path("user.id").entity(String.class).isEqualTo("1")
                .path("user.name").entity(String.class).isEqualTo("John Doe")
                .path("user.trips").entityList(Trip.class).hasSize(2)
                .path("user.trips[0].destination").entity(String.class).isEqualTo("Paris")
                .path("user.trips[1].destination").entity(String.class).isEqualTo("London");
    }

    @Test
    void user_WithTripsAndBookings_ReturnsFullNestedData() {
        User mockUser = User.builder()
                .id(1L)
                .name("John Doe")
                .build();

        Trip mockTrip = Trip.builder().id(1L).destination("Paris").build();

        List<Booking> mockBookings = List.of(
                Booking.builder().id(1L).status("CONFIRMED").build(),
                Booking.builder().id(2L).status("PENDING").build()
        );

        when(userClient.getUserById(eq(1L))).thenReturn(mockUser);
        when(tripClient.getTripsByUserId(eq(1L))).thenReturn(List.of(mockTrip));
        when(bookingClient.getBookingsByTripId(eq(1L))).thenReturn(mockBookings);

        graphQlTester.document("""
                query {
                    user(id: 1) {
                        id
                        name
                        trips {
                            id
                            destination
                            bookings {
                                id
                                status
                            }
                        }
                    }
                }
                """)
                .execute()
                .path("user.id").entity(String.class).isEqualTo("1")
                .path("user.trips[0].destination").entity(String.class).isEqualTo("Paris")
                .path("user.trips[0].bookings").entityList(Booking.class).hasSize(2)
                .path("user.trips[0].bookings[0].status").entity(String.class).isEqualTo("CONFIRMED");
    }
}
