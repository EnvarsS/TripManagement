package com.kafka.test.client;

import com.kafka.test.FeignConfig;
import com.kafka.test.dto.TripDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "trip-service", url = "${SERVICES_TRIP_URL:localhost:8082}", configuration = FeignConfig.class)
@Service
public interface TripClient {
    @GetMapping("/trips/user/{userId}")
    List<TripDTO> getTripsByUserId(@PathVariable("userId") Long userId);
}
