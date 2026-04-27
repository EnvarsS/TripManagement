package com.kafka.gateway.controller;

import com.kafka.gateway.client.TripClient;
import com.kafka.gateway.client.UserClient;
import com.kafka.gateway.dto.BookingDTO;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BookingMutationController {
    private final UserClient userClient;
    private final TripClient tripClient;

    public BookingMutationController(UserClient userClient, TripClient tripClient) {
        this.userClient = userClient;
        this.tripClient = tripClient;
    }

    @MutationMapping
    public BookingDTO createBooking(@Argument BookingInput input) {
        // Перевірка існування користувача та туру через Feign перед бронюванням
        userClient.getUserById(input.getId());
        tripClient.getTripById(input.getId());

        return bookingService.book(input.getId(), input.getId());
    }
}
