package com.kafka.gateway.client;

import com.kafka.gateway.FeignConfig;
import com.kafka.gateway.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Припускаємо, що сервіси працюють у Docker (наприклад, user-service)
@FeignClient(name = "user-service", url = "${SERVICES_USER_URL:localhost:8081}", configuration = FeignConfig.class)
public interface UserClient {
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
}
