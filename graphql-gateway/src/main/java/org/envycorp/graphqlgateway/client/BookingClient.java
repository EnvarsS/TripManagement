package org.envycorp.graphqlgateway.client;

import org.envycorp.graphqlgateway.model.Booking;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "booking-service", url = "${backend.services.booking-service.url}")
public interface BookingClient {

    @GetMapping("/trips/{tripId}/bookings")
    List<Booking> getBookingsByTripId(@PathVariable("tripId") Long tripId);
}
