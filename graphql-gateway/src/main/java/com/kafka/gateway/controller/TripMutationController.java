package com.kafka.gateway.controller;

import com.kafka.gateway.client.UserClient;
import com.kafka.gateway.dto.TripDTO;
import com.kafka.gateway.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TripMutationController {
    private final UserClient userClient;
    public TripMutationController(UserClient userClient) {
        this.userClient = userClient;
    }

    @MutationMapping
    public TripDTO createTrip(@Argument TripInput input) {
        return tripService.create(input);
    }

    @MutationMapping
    public TripDTO updateTrip(@Argument String id, @Argument TripInput input) {
        return tripService.update(id, input);
    }
}