package com.kafka.gateway.client;

import com.kafka.gateway.FeignConfig;
import com.kafka.gateway.dto.BookingDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "booking-service", url = "${SERVICES_BOOKING_URL:localhost:8083}", configuration = FeignConfig.class)
public interface BookingClient {
    @GetMapping("/bookings/trip/{tripId}")
    List<BookingDTO> getBookingsByTripId(@PathVariable("tripId") Long tripId);
}