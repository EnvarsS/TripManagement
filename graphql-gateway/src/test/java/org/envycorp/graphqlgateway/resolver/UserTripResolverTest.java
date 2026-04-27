package org.envycorp.graphqlgateway.resolver;

import org.envycorp.graphqlgateway.client.TripClient;
import org.envycorp.graphqlgateway.model.Trip;
import org.envycorp.graphqlgateway.model.User;
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
class UserTripResolverTest {

    @Mock
    private TripClient tripClient;

    @InjectMocks
    private UserTripResolver userTripResolver;

    @Test
    void getTrips_ReturnsTrips() {
        User user = User.builder().id(1L).name("John").build();
        List<Trip> trips = List.of(
                Trip.builder().id(1L).destination("Paris").build(),
                Trip.builder().id(2L).destination("London").build()
        );

        when(tripClient.getTripsByUserId(1L)).thenReturn(trips);

        List<Trip> result = userTripResolver.getTrips(user);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDestination()).isEqualTo("Paris");
    }

    @Test
    void getTrips_ReturnsEmptyList() {
        User user = User.builder().id(1L).name("John").build();

        when(tripClient.getTripsByUserId(1L)).thenReturn(Collections.emptyList());

        List<Trip> result = userTripResolver.getTrips(user);

        assertThat(result).isEmpty();
    }
}
