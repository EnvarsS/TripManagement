package com.kafka.test.client;

import com.kafka.test.FeignConfig;
import com.kafka.test.dto.BookingDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "booking-service", url = "${SERVICES_BOOKING_URL:localhost:8083}", configuration = FeignConfig.class)
@Service
public interface BookingClient {
    @GetMapping("/bookings/trip/{tripId}")
    List<BookingDTO> getBookingsByTripId(@PathVariable("tripId") Long tripId);
}