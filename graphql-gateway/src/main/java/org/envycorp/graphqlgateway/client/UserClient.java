package org.envycorp.graphqlgateway.client;

import org.envycorp.commonmodule.dto.request.users.UserCreateRequestDto;
import org.envycorp.commonmodule.dto.request.users.UserLoginRequestDto;
import org.envycorp.commonmodule.dto.request.users.UserUpdateRequestDto;
import org.envycorp.commonmodule.dto.response.users.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", url = "${backend.services.user-service.url}")
public interface UserClient {

    @PostMapping("/auth/login")
    String login(@RequestBody UserLoginRequestDto request);

    @PostMapping("/auth/register")
    String register(@RequestBody UserCreateRequestDto request);

    @GetMapping("/users")
    UserResponseDto getCurrentUser();

    @GetMapping("/users/{id}")
    UserResponseDto getUserById(@PathVariable("id") Long id);

    @PostMapping("/users")
    UserResponseDto createUser(@RequestBody UserCreateRequestDto request);

    @PutMapping("/users/{id}")
    UserResponseDto updateUser(@PathVariable("id") Long id,
                               @RequestBody UserUpdateRequestDto request);

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable("id") Long id);
}
