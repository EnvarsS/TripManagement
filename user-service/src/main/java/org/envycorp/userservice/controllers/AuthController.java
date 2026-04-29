package org.envycorp.userservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.envycorp.commonmodule.dto.request.users.UserCreateRequestDto;
import org.envycorp.commonmodule.dto.request.users.UserLoginRequestDto;
import org.envycorp.userservice.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequestDto userLogin) {
        return authService.login(userLogin);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserCreateRequestDto userCreate) {
        return authService.signInAndCreateUser(userCreate);
    }
}
