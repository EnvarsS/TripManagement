package com.kafka.gateway.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import com.kafka.gateway.dto.UserDTO;

@Controller
public class UserMutationController {

    private final UserService userService;

    public UserMutationController() {
    }
    public UserMutationController(UserService userService) {
        this.userService = userService;
    }

    @MutationMapping
    public User createUser(@Argument UserInput input) {
        // Логіка створення: наприклад, виклик сервісу або репозиторію
        return userService.save(new User(input.getName(), input.getEmail()));
    }

    @MutationMapping
    public User updateUser(@Argument String id, @Argument UserInput input) {
        return userService.update(id, input);
    }
}
