package com.kafka.gateway.client;

import com.kafka.gateway.FeignConfig;
import com.kafka.gateway.dto.TripDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "trip-service", url = "${SERVICES_TRIP_URL:localhost:8082}", configuration = FeignConfig.class)
public interface TripClient {
    @GetMapping("/trips/user/{userId}")
    List<TripDTO> getTripsByUserId(@PathVariable("userId") Long userId);
}
