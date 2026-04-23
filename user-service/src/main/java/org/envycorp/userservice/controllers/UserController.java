package org.envycorp.userservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.envycorp.userservice.models.dto.request.UserCreateRequestDto;
import org.envycorp.userservice.models.dto.request.UserLoginRequestDto;
import org.envycorp.userservice.models.dto.request.UserUpdateRequestDto;
import org.envycorp.userservice.models.dto.response.UserResponseDto;
import org.envycorp.userservice.models.entity.User;
import org.envycorp.userservice.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public String login(@RequestBody UserLoginRequestDto userLogin) {
        return userService.login(userLogin);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserCreateRequestDto userCreate) {
        return userService.createUser(userCreate);
    }

    @GetMapping("/me")
    public UserResponseDto getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PutMapping("/me")
    public UserResponseDto updateUser(@Valid @RequestBody UserUpdateRequestDto dto) {
        return userService.updateCurrentUser(dto);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser() {
        userService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }
}
