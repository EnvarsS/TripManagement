package org.envycorp.graphqlgateway.client;

import org.envycorp.graphqlgateway.model.Trip;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "trip-service", url = "${backend.services.trip-service.url}")
public interface TripClient {

    @GetMapping("/users/{userId}/trips")
    List<Trip> getTripsByUserId(@PathVariable("userId") Long userId);
}
