package org.envycorp.userservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.envycorp.commonmodule.dto.request.users.UserCreateRequestDto;
import org.envycorp.commonmodule.dto.request.users.UserUpdateRequestDto;
import org.envycorp.commonmodule.dto.response.users.UserResponseDto;
import org.envycorp.userservice.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponseDto> getUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateRequestDto dto) {
        return userService.createUser(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("principal == #id or hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequestDto dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("principal == #id or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
