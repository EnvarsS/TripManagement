package org.envycorp.graphqlgateway.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.graphqlgateway.client.TripClient;
import org.envycorp.graphqlgateway.model.Trip;
import org.envycorp.graphqlgateway.model.User;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserTripResolver {

    private final TripClient tripClient;

    @SchemaMapping(typeName = "User", field = "trips")
    public List<Trip> getTrips(User user) {
        log.info("[GraphQL] Resolving trips for user id={}", user.getId());
        return tripClient.getTripsByUserId(user.getId());
    }
}
