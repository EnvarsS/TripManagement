package org.envycorp.userservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.envycorp.userservice.models.dto.request.UserCreateRequestDto;
import org.envycorp.userservice.models.dto.request.UserLoginRequestDto;
import org.envycorp.userservice.models.dto.request.UserUpdateRequestDto;
import org.envycorp.userservice.models.dto.response.UserResponseDto;
import org.envycorp.userservice.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        return userService.getUser();
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody UserUpdateRequestDto dto) {
        return userService.updateUser(dto);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.noContent().build();
    }
}
