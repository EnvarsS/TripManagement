package org.envycorp.userservice.controllers;

import lombok.RequiredArgsConstructor;
import org.envycorp.userservice.services.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

}
